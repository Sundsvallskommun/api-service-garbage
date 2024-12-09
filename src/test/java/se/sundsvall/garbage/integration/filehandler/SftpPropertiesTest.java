package se.sundsvall.garbage.integration.filehandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.garbage.Application;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class SftpPropertiesTest {

	@Autowired
	private SftpProperties properties;

	@Test
	void verifyPropertiesAreSet() {
		assertThat(properties.username()).isEqualTo("user");
		assertThat(properties.password()).isEqualTo("password");
		assertThat(properties.remoteHost()).isEqualTo("localhost");
		assertThat(properties.filename()).isEqualTo("schedule.csv");
	}

}
