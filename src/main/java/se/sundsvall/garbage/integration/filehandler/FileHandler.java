package se.sundsvall.garbage.integration.filehandler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import se.sundsvall.garbage.api.model.enums.FacilityCategory;
import se.sundsvall.garbage.api.model.enums.WasteType;
import se.sundsvall.garbage.integration.db.entity.GarbageScheduleEntity;
import tools.jackson.databind.MappingIterator;
import tools.jackson.dataformat.csv.CsvMapper;
import tools.jackson.dataformat.csv.CsvSchema;

/**
 * Handles downloading and parsing of garbage schedule CSV files from an SFTP server.
 */
@Component
@EnableConfigurationProperties(SftpProperties.class)
public class FileHandler {

	private static final String TEMP_FILE = System.getProperty("java.io.tmpdir") + "/schedule.csv";

	// The commons-vfs2/JSch SFTP input stream turns each read into a synchronous request→response, so
	// a small buffer means thousands of round-trips. Over a high-latency path that collapses throughput
	// (an 8 MB file took ~10 min in production). A 64 KB buffer cuts the round-trip count dramatically.
	private static final int DOWNLOAD_BUFFER_SIZE = 64 * 1024;

	private static final Logger log = LoggerFactory.getLogger(FileHandler.class);

	private static final DateTimeFormatter PICKUP_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private static final Pattern ADDRESS_PATTERN = Pattern.compile("^(.+?)\\s++(\\d++)\\s*+(.*)$");

	private final SftpProperties sftpProperties;

	public FileHandler(final SftpProperties sftpProperties) {
		this.sftpProperties = sftpProperties;
	}

	/**
	 * Downloads the garbage schedule CSV file from the configured SFTP server to a local temporary file.
	 */
	public void downloadFile() {
		final var start = System.nanoTime();

		// Use a private manager rather than the process-wide VFS.getManager() singleton, and close it
		// when done. The shared manager caches the SftpFileSystem — session and channel — indefinitely,
		// and SftpFileSystem.getChannel() hands back its cached idleChannel WITHOUT revalidating the
		// session. Since sessionTimeout closes the idle session shortly after a run, the next run would
		// otherwise pick up a dead channel and fail with "Pipe closed". Owning the manager keeps the
		// session alive only for the duration of the transfer, so nothing stale can be cached or shared.
		final var manager = new StandardFileSystemManager();
		try {
			manager.init();

			// Bound the transfer so a stalled connection fails fast instead of hanging the whole
			// scheduled job. connectTimeout caps the TCP/SSH handshake; sessionTimeout is the socket
			// read timeout, so a mid-transfer stall (no bytes for that long) aborts rather than idling.
			final var options = new FileSystemOptions();
			final var builder = SftpFileSystemConfigBuilder.getInstance();
			Optional.ofNullable(sftpProperties.connectTimeout()).ifPresent(timeout -> builder.setConnectTimeout(options, timeout));
			Optional.ofNullable(sftpProperties.sessionTimeout()).ifPresent(timeout -> builder.setSessionTimeout(options, timeout));

			final var local = manager.resolveFile(TEMP_FILE);
			final var remote = manager.resolveFile(String.format("sftp://%s:%s@%s/%s",
				sftpProperties.username(),
				sftpProperties.password(),
				sftpProperties.remoteHost(),
				sftpProperties.filename()), options);
			try {
				// Stream through a large buffer rather than copyFrom's default: buffering forces the SFTP
				// channel to serve large reads, so an 8 MB file is ~128 round-trips instead of ~1000+.
				try (final var in = new BufferedInputStream(remote.getContent().getInputStream(), DOWNLOAD_BUFFER_SIZE);
					final var out = new BufferedOutputStream(local.getContent().getOutputStream(), DOWNLOAD_BUFFER_SIZE)) {
					in.transferTo(out);
				}
			} finally {
				local.close();
				remote.close();
			}
			log.info("Downloaded schedule file ({} bytes) in {} ms", fileSize(), elapsedMs(start));
		} catch (final IOException e) {
			// Fail loudly: a swallowed download failure lets parseFile() run against a missing or stale
			// temp file, which surfaces as the misleading "Schedule file did not contain any rows".
			throw new IllegalStateException("Failed to download garbage schedule file from SFTP after " + elapsedMs(start) + " ms", e);
		} finally {
			manager.close();
		}
	}

	/**
	 * Parses the downloaded CSV file into a list of {@link GarbageScheduleEntity}. The temporary file is deleted after
	 * parsing. Returns an empty list if parsing fails.
	 *
	 * @return a list of parsed entities, or an empty list on error
	 */
	public List<GarbageScheduleEntity> parseFile() {
		final var start = System.nanoTime();
		final var csvMapper = new CsvMapper();
		final var schema = buildSchema();

		try (final MappingIterator<ParsedRow> it = csvMapper.readerFor(ParsedRow.class)
			.with(schema)
			.readValues(new FileReader(TEMP_FILE, StandardCharsets.UTF_8))) {
			final var result = it.readAll().stream()
				.map(this::mapToEntity)
				.toList();

			Files.delete(Path.of(TEMP_FILE));
			log.info("Parsed {} rows from schedule file in {} ms", result.size(), elapsedMs(start));
			return result;
		} catch (final Exception e) {
			log.info("Something went wrong parsing file (after {} ms)", elapsedMs(start), e);
			return Collections.emptyList();
		}
	}

	private static long elapsedMs(final long startNanos) {
		return (System.nanoTime() - startNanos) / 1_000_000;
	}

	private static long fileSize() {
		try {
			return Files.size(Path.of(TEMP_FILE));
		} catch (final IOException e) {
			return -1;
		}
	}

	/**
	 * Maps a parsed CSV row to a {@link GarbageScheduleEntity}, extracting street, house number, and additional information
	 * from the full address field.
	 *
	 * @param  row the parsed CSV row
	 * @return     the mapped entity
	 */
	GarbageScheduleEntity mapToEntity(final ParsedRow row) {
		final var fullAddress = Optional.ofNullable(row.getFullAddress())
			.map(String::trim)
			.orElse("");
		final var matcher = ADDRESS_PATTERN.matcher(fullAddress);

		final String street;
		final String houseNumber;
		final String additionalInformation;

		if (matcher.matches()) {
			street = matcher.group(1).trim();
			houseNumber = matcher.group(2).trim();
			additionalInformation = matcher.group(3).trim();
		} else {
			street = fullAddress;
			houseNumber = "";
			additionalInformation = "";
		}

		return GarbageScheduleEntity.builder()
			.withStreet(street)
			.withPostalCode(Optional.ofNullable(row.getPostalCode()).map(String::trim).orElse(null))
			.withHouseNumber(houseNumber)
			.withCity(Optional.ofNullable(row.getCity()).map(String::trim).orElse(null))
			.withAdditionalInformation(additionalInformation)
			.withFacilityCategory(FacilityCategory.forValue(Optional.ofNullable(row.getFacilityCategory()).map(String::trim).orElse(null)))
			.withDriveSchedule(Optional.ofNullable(row.getDriveSchedule()).map(String::trim).orElse(null))
			.withWasteType(WasteType.forValue(Optional.ofNullable(row.getWasteType()).map(String::trim).filter(s -> !s.isEmpty()).orElse(null)))
			.withNextPickupDate(parsePickupDate(row.getNextPickupDate()))
			.build();
	}

	/**
	 * Parses a pickup date string in "yyyy-MM-dd HH:mm:ss" format to a {@link LocalDate}.
	 *
	 * @param  dateStr the date string to parse, or {@code null}
	 * @return         the parsed date, or {@code null} if the input is null, empty, or blank
	 */
	private LocalDate parsePickupDate(final String dateStr) {
		return Optional.ofNullable(dateStr)
			.map(String::trim)
			.filter(s -> !s.isEmpty())
			.map(s -> LocalDate.parse(s, PICKUP_DATE_FORMATTER))
			.orElse(null);
	}

	/**
	 * Builds the CSV schema defining the column order and semicolon separator for the schedule file. The first three
	 * columns (id1, id2, id3) are unused identifiers from the source system and are discarded during mapping.
	 *
	 * @return the configured {@link CsvSchema}
	 */
	private CsvSchema buildSchema() {
		return CsvSchema.builder()
			.addColumn("id1") // Unused source system identifier
			.addColumn("id2") // Unused source system identifier
			.addColumn("id3") // Unused source system identifier
			.addColumn("fullAddress")
			.addColumn("postalCode")
			.addColumn("city")
			.addColumn("wasteType")
			.addColumn("nextPickupDate")
			.addColumn("driveSchedule")
			.addColumn("facilityCategory")
			.build().withColumnSeparator(';');
	}

}
