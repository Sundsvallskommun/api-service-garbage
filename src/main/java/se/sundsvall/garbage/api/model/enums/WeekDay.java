package se.sundsvall.garbage.api.model.enums;

import java.util.Arrays;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum WeekDay {
	MONDAY("MÅ"),
	TUESDAY("TI"),
	WEDNESDAY("ON"),
	THURSDAY("TO"),
	FRIDAY("FR");

	final String shortCode;

	public static WeekDay forValue(final String shortCode) {
		return Arrays.stream(WeekDay.values())
			.filter(weekday -> weekday.shortCode.equals(shortCode))
			.findFirst()
			.orElse(null);
	}
}
