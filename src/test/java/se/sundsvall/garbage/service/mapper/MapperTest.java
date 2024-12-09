package se.sundsvall.garbage.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import se.sundsvall.garbage.api.model.GarbageScheduleResponse;
import se.sundsvall.garbage.api.model.enums.FacilityCategory;
import se.sundsvall.garbage.api.model.enums.Week;
import se.sundsvall.garbage.api.model.enums.WeekDay;
import se.sundsvall.garbage.integration.db.entity.GarbageScheduleEntity;

class MapperTest {

	@Test
	void entityToResponseShouldMapCorrectly() {
		final GarbageScheduleEntity entity = new GarbageScheduleEntity();
		entity.setStreet("Test Street");
		entity.setHouseNumber("123");
		entity.setPostalCode("45678");
		entity.setCity("Test City");
		entity.setAdditionalInformation("Test Info");
		entity.setFacilityCategory(FacilityCategory.VILLA);
		entity.setDriveSchedule("KP50TI2");

		final GarbageScheduleResponse response = Mapper.entityToResponse(entity);

		assertThat(response.getAddress().getStreet()).isEqualTo("Test Street");
		assertThat(response.getAddress().getHouseNumber()).isEqualTo("123");
		assertThat(response.getAddress().getPostalCode()).isEqualTo("45678");
		assertThat(response.getAddress().getCity()).isEqualTo("Test City");
		assertThat(response.getAdditionalInformation()).isEqualTo("Test Info");
		assertThat(response.getFacilityCategory()).isEqualTo(FacilityCategory.VILLA);
		assertThat(response.getGarbageScheduledDay()).isEqualTo(WeekDay.TUESDAY);
		assertThat(response.getGarbageScheduledWeek()).isEqualTo(Week.EVEN);
	}

	@Test
	void entityToResponseShouldHandleNullEntity() {

		assertThat(Mapper.entityToResponse(null)).isNull();
	}

	@Test
	void entityToResponseShouldHandleEmptyDriveSchedule() {
		final GarbageScheduleEntity entity = new GarbageScheduleEntity();
		entity.setDriveSchedule("");

		assertThat(Mapper.entityToResponse(entity).getGarbageScheduledDay()).isNull();
	}

}
