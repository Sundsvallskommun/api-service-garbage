package se.sundsvall.garbage.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
public class GarbageScheduleRequest {

	@Schema(description = "Address", types = {
		"string", "null"
	}, examples = "Testgatan")
	private String street;

	@Schema(description = "Address", types = {
		"string", "null"
	}, examples = "12")
	private String houseNumber;

	@Schema(description = "Zipcode", types = {
		"string", "null"
	}, examples = "85731")
	private String postalCode;

	@Schema(description = "City", types = {
		"string", "null"
	}, example = "Sundsvall")
	private String city;

	@Schema(description = "Any other identifier. Example HouseLetter or building clarification", examples = "A", types = {
		"string", "null"
	})
	private String additionalInformation;

	@Schema(description = "Page number", examples = "1", minimum = "1")
	@Min(1)
	private Integer page;

	@Schema(description = "Result size per page. If omitted, all results are returned.", examples = "100", minimum = "1")
	@Min(1)
	private Integer limit;

}
