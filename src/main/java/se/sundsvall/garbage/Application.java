package se.sundsvall.garbage;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import se.sundsvall.dept44.ServiceApplication;

@ServiceApplication
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT2M")
@EnableAsync
public class Application {
	public static void main(final String... args) {
		run(Application.class, args);
	}
}
