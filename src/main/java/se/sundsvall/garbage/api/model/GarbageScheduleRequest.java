package se.sundsvall.garbage.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
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

	@Schema(description = "Address", nullable = true, example = "Testgatan")
	private String street;

	@Schema(description = "Address", nullable = true, example = "12")
	private String houseNumber;

	@Schema(description = "Zipcode", nullable = true, example = "85731")
	private String postalCode;

	@Schema(description = "City", nullable = true, example = "Sundsvall")
	private String city;

	@Schema(description = "Any other identifier. Example HouseLetter or building clarification",
		example = "A",
		nullable = true)
	private String additionalInformation;

	@Builder.Default
	@Schema(description = "Page number", example = "1", defaultValue = "1")
	@Min(1)
	private int page = 1;

	@Builder.Default
	@Schema(description = "Result size per page", example = "100", defaultValue = "20")
	@Min(1)
	@Max(1000)
	private int limit = 20;

}
