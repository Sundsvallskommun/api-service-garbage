package se.sundsvall.garbage;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import se.sundsvall.dept44.ServiceApplication;

@ServiceApplication
@EnableScheduling
@EnableAsync
public class Application {
	public static void main(final String... args) {
		run(Application.class, args);
	}
}
