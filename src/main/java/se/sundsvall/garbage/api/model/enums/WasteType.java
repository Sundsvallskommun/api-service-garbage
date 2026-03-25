package se.sundsvall.garbage.api.model.enums;

import java.util.Arrays;

public enum WasteType {
	WASTE("Restavfall"),
	FOOD("Matavfall"),
	PLASTIC("Plastförp."),
	PAPER("Pappersförp.");

	private final String code;

	WasteType(final String code) {
		this.code = code;
	}

	public static WasteType forValue(final String code) {
		if (code == null) {
			return null;
		}
		return Arrays.stream(WasteType.values())
			.filter(wasteType -> wasteType.code.equals(code))
			.findFirst()
			.orElse(null);
	}

}
