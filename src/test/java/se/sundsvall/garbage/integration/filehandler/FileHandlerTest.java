package se.sundsvall.garbage.integration.filehandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.garbage.api.model.FacilityCategory;

@ExtendWith(MockitoExtension.class)
class FileHandlerTest {

	@Spy
	private SftpProperties sftpProperties;

	@InjectMocks
	private FileHandler fileHandler;

	@Test
	void parseFile() {

		final var filePath = Path.of(System.getProperty("java.io.tmpdir") + "/schedule.csv");
		final var file = new File("src/test/resources/mockfiles/schedule.csv");

		if (file.exists()) {
			try (var inputStream = new FileInputStream(file)) {
				Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}

		}

		final var result = fileHandler.parseFile();

		assertThat(result)
			.isNotNull()
			.hasSize(2);

		final var firstRow = result.get(0);
		assertThat(firstRow.getDriveSchedule()).isEqualTo("KP50TI2");
		assertThat(firstRow.getId()).isNull();
		assertThat(firstRow.getCity()).isEqualTo("Sundsvall");
		assertThat(firstRow.getStreet()).isEqualTo("Testgatan");
		assertThat(firstRow.getHouseNumber()).isEqualTo("12");
		assertThat(firstRow.getPostalCode()).isEqualTo("85185 ");
		assertThat(firstRow.getCity()).isEqualTo("Sundsvall");
		assertThat(firstRow.getFacilityCategory()).isEqualTo(FacilityCategory.VILLA);
		assertThat(firstRow.getAdditionalInformation()).isEmpty();
	}

	@Test
	void parseFileAndExpectError() {

		final var result = fileHandler.parseFile();

		assertThat(result).isNull();

	}

	@Test
	void downloadFile() {
		fileHandler.downloadFile();

		verify(sftpProperties).getUsername();
		verify(sftpProperties).getPassword();
		verify(sftpProperties).getRemoteHost();
		verify(sftpProperties).getFilename();
	}
}
