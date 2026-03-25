package se.sundsvall.garbage.api.model.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WasteTypeTest {

	@Test
	void testEnumValues() {
		assertThat(WasteType.values()).containsExactlyInAnyOrder(WasteType.WASTE, WasteType.FOOD, WasteType.PLASTIC, WasteType.PAPER);
	}

	@Test
	void testForValue() {
		assertThat(WasteType.forValue("Restavfall")).isEqualTo(WasteType.WASTE);
		assertThat(WasteType.forValue("Matavfall")).isEqualTo(WasteType.FOOD);
		assertThat(WasteType.forValue("Plastförp.")).isEqualTo(WasteType.PLASTIC);
		assertThat(WasteType.forValue("Pappersförp.")).isEqualTo(WasteType.PAPER);
		assertThat(WasteType.forValue("")).isNull();
		assertThat(WasteType.forValue(null)).isNull();
		assertThat(WasteType.forValue("foo")).isNull();
	}

}
