package se.sundsvall.garbage.api.model.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WeekTest {

	@Test
	void testEnumValues() {
		assertThat(Week.values()).containsExactlyInAnyOrder(Week.ODD, Week.EVEN);
	}

}
