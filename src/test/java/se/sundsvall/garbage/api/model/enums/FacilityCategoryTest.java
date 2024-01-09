package se.sundsvall.garbage.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FacilityCategoryTest {

	@Test
	void testEnumValues() {
		assertThat(FacilityCategory.values()).containsExactlyInAnyOrder(FacilityCategory.VILLA, FacilityCategory.FRITIDSHUS);
	}

	@Test
	void testForValue() {
		assertThat(FacilityCategory.forValue("VI")).isEqualTo(FacilityCategory.VILLA);
		assertThat(FacilityCategory.forValue("FH")).isEqualTo(FacilityCategory.FRITIDSHUS);
		assertThat(FacilityCategory.forValue("")).isNull();
		assertThat(FacilityCategory.forValue(null)).isNull();
		assertThat(FacilityCategory.forValue("foo")).isNull();
	}

}
