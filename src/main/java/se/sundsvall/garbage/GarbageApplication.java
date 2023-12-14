package se.sundsvall.garbage;

import static org.springframework.boot.SpringApplication.run;

import se.sundsvall.dept44.ServiceApplication;

@ServiceApplication
public class GarbageApplication {
	public static void main(String... args) {
		run(GarbageApplication.class, args);
	}
}
