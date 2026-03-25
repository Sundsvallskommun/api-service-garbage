package se.sundsvall.garbage.integration.filehandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.garbage.api.model.enums.FacilityCategory;
import se.sundsvall.garbage.api.model.enums.WasteType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileHandlerTest {

	@Mock
	private SftpProperties sftpProperties;

	@InjectMocks
	private FileHandler fileHandler;

	@Test
	void downloadFile() {
		when(sftpProperties.username()).thenReturn("username");
		when(sftpProperties.password()).thenReturn("password");
		when(sftpProperties.remoteHost()).thenReturn("remoteHost");

		fileHandler.downloadFile();

		verify(sftpProperties).username();
		verify(sftpProperties).password();
		verify(sftpProperties).remoteHost();
	}

	@Test
	void parseFile() {
		final var filePath = Path.of(System.getProperty("java.io.tmpdir") + "/schedule.csv");
		final var file = new File("src/test/resources/mockfiles/schedule.csv");

		if (file.exists()) {
			try (final var inputStream = new FileInputStream(file)) {
				Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}

		final var result = fileHandler.parseFile();

		assertThat(result)
			.isNotNull()
			.hasSize(4);

		// Row 1
		final var firstRow = result.getFirst();
		assertThat(firstRow.getStreet()).isEqualTo("Test Street");
		assertThat(firstRow.getHouseNumber()).isEqualTo("1");
		assertThat(firstRow.getAdditionalInformation()).isEmpty();
		assertThat(firstRow.getCity()).isEqualTo("Test City");
		assertThat(firstRow.getFacilityCategory()).isEqualTo(FacilityCategory.VILLA);
		assertThat(firstRow.getDriveSchedule()).isEqualTo("U11 TI2");
		assertThat(firstRow.getWasteType()).isEqualTo(WasteType.WASTE);
		assertThat(firstRow.getNextPickupDate()).isEqualTo(LocalDate.of(2026, 1, 1));
		assertThat(firstRow.getPostalCode()).isEqualTo("12345");

		// Row 2
		final var secondRow = result.get(1);
		assertThat(secondRow.getStreet()).isEqualTo("Test Road");
		assertThat(secondRow.getHouseNumber()).isEqualTo("18");
		assertThat(secondRow.getAdditionalInformation()).isEmpty();
		assertThat(secondRow.getFacilityCategory()).isEqualTo(FacilityCategory.VILLA);
		assertThat(secondRow.getWasteType()).isEqualTo(WasteType.WASTE);

		// Row 3
		final var thirdRow = result.get(2);
		assertThat(thirdRow.getStreet()).isEqualTo("Test Road");
		assertThat(thirdRow.getHouseNumber()).isEqualTo("20");
		assertThat(thirdRow.getAdditionalInformation()).isEmpty();
		assertThat(thirdRow.getFacilityCategory()).isEqualTo(FacilityCategory.VILLA);

		// Row 4
		final var fourthRow = result.get(3);
		assertThat(fourthRow.getStreet()).isEqualTo("Test Avenue");
		assertThat(fourthRow.getHouseNumber()).isEqualTo("4");
		assertThat(fourthRow.getAdditionalInformation()).isEmpty();
		assertThat(fourthRow.getDriveSchedule()).isEqualTo("U14 FR2");
	}

	@Test
	void parseFileAndExpectError() {
		final var result = fileHandler.parseFile();

		assertThat(result).isEmpty();
	}

	@Test
	void mapToEntityWithNullFields() {
		final var row = new ParsedRow();

		final var entity = fileHandler.mapToEntity(row);

		assertThat(entity.getStreet()).isEmpty();
		assertThat(entity.getHouseNumber()).isNull();
		assertThat(entity.getAdditionalInformation()).isNull();
		assertThat(entity.getPostalCode()).isNull();
		assertThat(entity.getCity()).isNull();
		assertThat(entity.getWasteType()).isNull();
		assertThat(entity.getNextPickupDate()).isNull();
		assertThat(entity.getDriveSchedule()).isNull();
		assertThat(entity.getFacilityCategory()).isNull();
	}

}
