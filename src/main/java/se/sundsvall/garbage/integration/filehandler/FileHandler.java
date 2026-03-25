package se.sundsvall.garbage.integration.filehandler;

import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.VFS;
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

@Component
@EnableConfigurationProperties(SftpProperties.class)
public class FileHandler {

	private static final String TEMP_FILE = System.getProperty("java.io.tmpdir") + "/schedule.csv";

	private static final Logger log = LoggerFactory.getLogger(FileHandler.class);

	private static final DateTimeFormatter PICKUP_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private static final Pattern ADDRESS_PATTERN = Pattern.compile("^(.+?)\\s+(\\d+)\\s*(.*)$");

	private final SftpProperties sftpProperties;

	public FileHandler(final SftpProperties sftpProperties) {
		this.sftpProperties = sftpProperties;
	}

	public void downloadFile() {
		try {
			final var manager = VFS.getManager();
			final var local = manager.resolveFile(TEMP_FILE);
			final var remote = manager.resolveFile(String.format("sftp://%s:%s@%s/%s",
				sftpProperties.username(),
				sftpProperties.password(),
				sftpProperties.remoteHost(),
				sftpProperties.filename()));
			local.copyFrom(remote, Selectors.SELECT_SELF);
			local.close();
			remote.close();
		} catch (final FileSystemException e) {
			log.info("Something went wrong downloading file", e);
		}
	}

	public List<GarbageScheduleEntity> parseFile() {
		final var csvMapper = new CsvMapper();
		final var schema = buildSchema();

		try (final MappingIterator<ParsedRow> it = csvMapper.readerFor(ParsedRow.class)
			.with(schema)
			.readValues(new FileReader(TEMP_FILE, StandardCharsets.UTF_8))) {
			final var result = it.readAll().stream()
				.map(this::mapToEntity)
				.toList();

			Files.delete(Path.of(TEMP_FILE));
			return result;
		} catch (final Exception e) {
			log.info("Something went wrong parsing file", e);
			return Collections.emptyList();
		}
	}

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
			houseNumber = null;
			additionalInformation = null;
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

	private LocalDate parsePickupDate(final String dateStr) {
		return Optional.ofNullable(dateStr)
			.map(String::trim)
			.filter(s -> !s.isEmpty())
			.map(s -> LocalDate.parse(s, PICKUP_DATE_FORMATTER))
			.orElse(null);
	}

	private CsvSchema buildSchema() {
		return CsvSchema.builder()
			.addColumn("id1")
			.addColumn("id2")
			.addColumn("id3")
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
