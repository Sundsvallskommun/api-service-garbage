package se.sundsvall.garbage.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class WeekTest {

	@Test
	void testEnumValues() {
		assertThat(Week.values()).containsExactlyInAnyOrder(Week.ODD, Week.EVEN);
	}

}
