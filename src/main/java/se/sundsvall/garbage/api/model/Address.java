package se.sundsvall.garbage.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Address {
	@NotEmpty
	@NotNull
	@Schema(description = "Address", examples = "Testgatan")
	private String street;

	@NotEmpty
	@NotNull
	@Schema(description = "Address", examples = "12")
	private String houseNumber;

	@NotEmpty
	@NotNull
	@Schema(description = "Zipcode", examples = "85731")
	private String postalCode;

	@Schema(description = "City", types = {
		"string", "null"
	}, examples = "Sundsvall")
	private String city;
}
