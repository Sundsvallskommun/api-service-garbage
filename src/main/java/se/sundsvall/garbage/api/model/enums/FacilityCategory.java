package se.sundsvall.garbage.api.model.enums;

import java.util.Arrays;

public enum FacilityCategory {
	VILLA("SMÅHUS"),
	FRITIDSHUS("FRIHUS");

	private final String code;

	FacilityCategory(final String code) {
		this.code = code;
	}

	public static FacilityCategory forValue(final String code) {
		if (code == null) {
			return null;
		}
		return Arrays.stream(FacilityCategory.values())
			.filter(facilityCategory -> facilityCategory.code.equals(code))
			.findFirst()
			.orElse(null);
	}

}
