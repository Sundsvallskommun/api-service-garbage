package se.sundsvall.garbage.api.model.enums;

import java.util.Arrays;

public enum WeekDay {
	MONDAY("MÅ"),
	TUESDAY("TI"),
	WEDNESDAY("ON"),
	THURSDAY("TO"),
	FRIDAY("FR");

	final String shortCode;

	WeekDay(final String shortCode) {
		this.shortCode = shortCode;
	}

	public static WeekDay forValue(final String shortCode) {
		return Arrays.stream(WeekDay.values())
			.filter(weekday -> weekday.shortCode.equals(shortCode))
			.findFirst()
			.orElse(null);
	}
}
