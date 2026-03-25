package se.sundsvall.garbage.integration.filehandler;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParsedRow {

	private String id1;

	private String id2;

	private String id3;

	private String fullAddress;

	private String postalCode;

	private String city;

	private String wasteType;

	private String nextPickupDate;

	private String driveSchedule;

	private String facilityCategory;

}
