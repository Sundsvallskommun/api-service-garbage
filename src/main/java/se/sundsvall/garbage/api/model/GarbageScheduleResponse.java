package se.sundsvall.garbage.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.garbage.api.model.enums.FacilityCategory;
import se.sundsvall.garbage.api.model.enums.Week;
import se.sundsvall.garbage.api.model.enums.WeekDay;

@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GarbageScheduleResponse {

	@Schema(description = "Address")
	private Address address;

	@Schema(description = "Any other identifier. Example HouseLetter or building clarification", examples = "A")
	private String additionalInformation;

	@Schema(description = "What weekday garbage is collected ", examples = "MONDAY")
	private WeekDay garbageScheduledDay;

	@Schema(description = "What week garbage is collected. Odd/even numbers ", examples = "ODD")
	private Week garbageScheduledWeek;

	@Schema(description = "Which type of facility ", examples = "VILLA")
	private FacilityCategory facilityCategory;

}
