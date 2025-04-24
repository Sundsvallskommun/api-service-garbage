package se.sundsvall.garbage.integration.filehandler;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.VFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import se.sundsvall.garbage.api.model.enums.FacilityCategory;
import se.sundsvall.garbage.integration.db.entity.GarbageScheduleEntity;

@Component
@EnableConfigurationProperties(SftpProperties.class)
public class FileHandler {

	private static final String TEMP_FILE = System.getProperty("java.io.tmpdir") + "/schedule.csv";

	private static final Logger log = LoggerFactory.getLogger(FileHandler.class);

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
				.filter(parsedRow -> !parsedRow.getDriveSchedule().startsWith("SL"))
				.map(this::mapToEntity)
				.toList();

			Files.delete(Path.of(TEMP_FILE));
			return result;
		} catch (final Exception e) {
			log.info("Something went wrong parsing file", e);
			return Collections.emptyList();
		}
	}

	private GarbageScheduleEntity mapToEntity(final ParsedRow row) {
		return GarbageScheduleEntity.builder()
			.withStreet(row.getAddress())
			.withHouseNumber(row.getAdressNumber())
			.withPostalCode(row.getZipcode())
			.withCity(row.getCity())
			.withAdditionalInformation(row.getAdditionalInformation())
			.withFacilityCategory(FacilityCategory.forValue(row.getFacilityCategory()))
			.withDriveSchedule(row.getDriveSchedule())
			.build();
	}

	private CsvSchema buildSchema() {
		return CsvSchema.builder()
			.addColumn("address")
			.addColumn("adressNumber")
			.addColumn("additionalInformation")
			.addColumn("facilityCategory")
			.addColumn("ignored")
			.addColumn("driveSchedule")
			.addColumn("zipcode")
			.addColumn("city")
			.build().withColumnSeparator(';');
	}

}
