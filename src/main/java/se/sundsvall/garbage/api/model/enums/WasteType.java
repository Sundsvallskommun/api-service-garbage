package se.sundsvall.garbage.api.model.enums;

import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum WasteType {
	WASTE(List.of("Restavfall")),
	FOOD(List.of("Matavfall")),
	PLASTIC(List.of("Plastförp.")),
	PAPER(List.of("Pappersförp."));

	final List<String> codes;

	public static WasteType forValue(final String code) {
		if (code == null) {
			return null;
		}
		return Arrays.stream(WasteType.values())
			.filter(wasteType -> wasteType.codes.contains(code))
			.findFirst()
			.orElse(null);
	}

}
