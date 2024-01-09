package se.sundsvall.garbage.api.model.enums;

import java.util.Arrays;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum FacilityCategory {
	VILLA("VI"),
	FRITIDSHUS("FH");

	final String shortCode;

	public static FacilityCategory forValue(final String shortCode) {
		return Arrays.stream(FacilityCategory.values())
			.filter(facilityCategory -> facilityCategory.shortCode.equals(shortCode))
			.findFirst()
			.orElse(null);
	}

}
