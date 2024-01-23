package se.sundsvall.garbage.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class WeekDayTest {

	@Test
	void testEnumValues() {
		assertThat(WeekDay.values()).containsExactlyInAnyOrder(WeekDay.MONDAY, WeekDay.TUESDAY, WeekDay.WEDNESDAY, WeekDay.THURSDAY, WeekDay.FRIDAY);
	}

	@Test
	void testForValue() {
		assertThat(WeekDay.forValue("MÅ")).isEqualTo(WeekDay.MONDAY);
		assertThat(WeekDay.forValue("TI")).isEqualTo(WeekDay.TUESDAY);
		assertThat(WeekDay.forValue("ON")).isEqualTo(WeekDay.WEDNESDAY);
		assertThat(WeekDay.forValue("TO")).isEqualTo(WeekDay.THURSDAY);
		assertThat(WeekDay.forValue("FR")).isEqualTo(WeekDay.FRIDAY);
		assertThat(WeekDay.forValue("")).isNull();
		assertThat(WeekDay.forValue(null)).isNull();
		assertThat(WeekDay.forValue("foo")).isNull();
	}

}
