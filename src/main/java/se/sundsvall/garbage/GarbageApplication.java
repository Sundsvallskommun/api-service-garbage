package se.sundsvall.garbage;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.scheduling.annotation.EnableScheduling;

import se.sundsvall.dept44.ServiceApplication;

@ServiceApplication
@EnableScheduling
public class GarbageApplication {

	public static void main(final String... args) {
		run(GarbageApplication.class, args);
	}

}
