package se.sundsvall.garbage.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import se.sundsvall.garbage.Application;

import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class ShedlockConfigurationTest {

	@Autowired
	private LockProvider lockProvider;

	@Test
	void testAutowiring() {
		assertThat(lockProvider).isNotNull();
	}

	@Test
	void testLockMechanism() {
		final var lockConfiguration = new LockConfiguration(Instant.now(), "lockName", Duration.ofSeconds(30), Duration.ofSeconds(0));
		final var lock = lockProvider.lock(lockConfiguration);

		assertThat(lock).isPresent();
		assertThat(lockProvider.lock(lockConfiguration)).isNotPresent();

		lock.get().unlock();
	}

}
