package se.sundsvall.garbage.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "Waste schedule entry")
public class WasteSchedule {

	@Schema(description = "Type of waste", examples = "WASTE", allowableValues = {
		"WASTE", "FOOD", "PLASTIC", "PAPER"
	})
	private String wasteType;

	@Schema(description = "Next scheduled pickup date", examples = "2026-03-31")
	private LocalDate nextPickupDate;

}
