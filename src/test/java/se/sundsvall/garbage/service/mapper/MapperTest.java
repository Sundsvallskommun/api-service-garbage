package se.sundsvall.garbage.service.mapper;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.garbage.api.model.enums.FacilityCategory;
import se.sundsvall.garbage.api.model.enums.WasteType;
import se.sundsvall.garbage.api.model.enums.Week;
import se.sundsvall.garbage.api.model.enums.WeekDay;
import se.sundsvall.garbage.integration.db.entity.GarbageScheduleEntity;

import static org.assertj.core.api.Assertions.assertThat;

class MapperTest {

	@Test
	void entityToResponseShouldMapCorrectly() {
		final var entity = new GarbageScheduleEntity();
		entity.setStreet("Test Street");
		entity.setHouseNumber("123");
		entity.setPostalCode("45678");
		entity.setCity("Test City");
		entity.setAdditionalInformation("Test Info");
		entity.setFacilityCategory(FacilityCategory.VILLA);
		entity.setDriveSchedule("KP50TI2");
		entity.setWasteType(WasteType.WASTE);
		entity.setNextPickupDate(LocalDate.of(2026, 3, 31));

		final var response = Mapper.entityToResponse(entity);

		assertThat(response.getAddress().getStreet()).isEqualTo("Test Street");
		assertThat(response.getAddress().getHouseNumber()).isEqualTo("123");
		assertThat(response.getAddress().getPostalCode()).isEqualTo("45678");
		assertThat(response.getAddress().getCity()).isEqualTo("Test City");
		assertThat(response.getAdditionalInformation()).isEqualTo("Test Info");
		assertThat(response.getFacilityCategory()).isEqualTo(FacilityCategory.VILLA);
		assertThat(response.getGarbageScheduledDay()).isEqualTo(WeekDay.TUESDAY);
		assertThat(response.getGarbageScheduledWeek()).isEqualTo(Week.EVEN);
		assertThat(response.getSchedules()).hasSize(1);
		assertThat(response.getSchedules().getFirst().getWasteType()).isEqualTo("WASTE");
		assertThat(response.getSchedules().getFirst().getNextPickupDate()).isEqualTo(LocalDate.of(2026, 3, 31));
	}

	@Test
	void entityToResponseShouldMapNewFormatDriveSchedule() {
		final var entity = new GarbageScheduleEntity();
		entity.setDriveSchedule("U11 TI2");

		final var response = Mapper.entityToResponse(entity);

		assertThat(response.getGarbageScheduledDay()).isEqualTo(WeekDay.TUESDAY);
		assertThat(response.getGarbageScheduledWeek()).isEqualTo(Week.EVEN);
	}

	@Test
	void entityToResponseShouldHandleOddWeekNewFormat() {
		final var entity = new GarbageScheduleEntity();
		entity.setDriveSchedule("U15 ON1");

		final var response = Mapper.entityToResponse(entity);

		assertThat(response.getGarbageScheduledDay()).isEqualTo(WeekDay.WEDNESDAY);
		assertThat(response.getGarbageScheduledWeek()).isEqualTo(Week.ODD);
	}

	@Test
	void entityToResponseShouldHandleNullEntity() {
		assertThat(Mapper.entityToResponse(null)).isNull();
	}

	@Test
	void entityToResponseShouldHandleEmptyDriveSchedule() {
		final var entity = new GarbageScheduleEntity();
		entity.setDriveSchedule("");

		assertThat(Mapper.entityToResponse(entity).getGarbageScheduledDay()).isNull();
	}

	@Test
	void entityToResponseShouldHandleNonStandardDriveSchedule() {
		final var entity = new GarbageScheduleEntity();
		entity.setDriveSchedule("TESTKÖRLISTA");

		final var response = Mapper.entityToResponse(entity);

		assertThat(response.getGarbageScheduledDay()).isNull();
		assertThat(response.getGarbageScheduledWeek()).isNull();
	}

	@Test
	void extractDayWeekPartShouldHandleNewFormat() {
		assertThat(Mapper.extractDayWeekPart("U11 TI2")).isEqualTo("TI2");
		assertThat(Mapper.extractDayWeekPart("U15 ON1")).isEqualTo("ON1");
		assertThat(Mapper.extractDayWeekPart("PZ39_FTL TI1")).isEqualTo("TI1");
	}

	@Test
	void extractDayWeekPartShouldHandleOldFormat() {
		assertThat(Mapper.extractDayWeekPart("KP50TI2")).isEqualTo("TI2");
		assertThat(Mapper.extractDayWeekPart("KP50FR1")).isEqualTo("FR1");
	}

	@Test
	void extractDayWeekPartShouldHandleEdgeCases() {
		assertThat(Mapper.extractDayWeekPart(null)).isNull();
		assertThat(Mapper.extractDayWeekPart("")).isNull();
		assertThat(Mapper.extractDayWeekPart("AB")).isNull();
	}

	@Test
	void entitiesToGroupedResponsesShouldGroupBySameAddress() {
		final var entity1 = GarbageScheduleEntity.builder()
			.withStreet("Testgatan")
			.withHouseNumber("1")
			.withPostalCode("85731")
			.withCity("Sundsvall")
			.withAdditionalInformation("A")
			.withFacilityCategory(FacilityCategory.VILLA)
			.withDriveSchedule("U11 TI2")
			.withWasteType(WasteType.WASTE)
			.withNextPickupDate(LocalDate.of(2026, 3, 31))
			.build();

		final var entity2 = GarbageScheduleEntity.builder()
			.withStreet("Testgatan")
			.withHouseNumber("1")
			.withPostalCode("85731")
			.withCity("Sundsvall")
			.withAdditionalInformation("A")
			.withFacilityCategory(FacilityCategory.VILLA)
			.withDriveSchedule("U11 TI2")
			.withWasteType(WasteType.FOOD)
			.withNextPickupDate(LocalDate.of(2026, 4, 2))
			.build();

		final var result = Mapper.entitiesToGroupedResponses(List.of(entity1, entity2));

		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getAddress().getStreet()).isEqualTo("Testgatan");
		assertThat(result.getFirst().getSchedules()).hasSize(2);
		assertThat(result.getFirst().getSchedules().get(0).getWasteType()).isEqualTo("WASTE");
		assertThat(result.getFirst().getSchedules().get(0).getNextPickupDate()).isEqualTo(LocalDate.of(2026, 3, 31));
		assertThat(result.getFirst().getSchedules().get(1).getWasteType()).isEqualTo("FOOD");
		assertThat(result.getFirst().getSchedules().get(1).getNextPickupDate()).isEqualTo(LocalDate.of(2026, 4, 2));
	}

	@Test
	void entitiesToGroupedResponsesShouldNotGroupDifferentAddresses() {
		final var entity1 = GarbageScheduleEntity.builder()
			.withStreet("Testgatan")
			.withHouseNumber("1")
			.withDriveSchedule("U11 TI2")
			.withWasteType(WasteType.WASTE)
			.withNextPickupDate(LocalDate.of(2026, 3, 31))
			.build();

		final var entity2 = GarbageScheduleEntity.builder()
			.withStreet("Testgatan")
			.withHouseNumber("2")
			.withDriveSchedule("U11 TI2")
			.withWasteType(WasteType.WASTE)
			.withNextPickupDate(LocalDate.of(2026, 3, 31))
			.build();

		final var result = Mapper.entitiesToGroupedResponses(List.of(entity1, entity2));

		assertThat(result).hasSize(2);
	}

	@Test
	void entitiesToGroupedResponsesShouldHandleNullAndEmptyList() {
		assertThat(Mapper.entitiesToGroupedResponses(null)).isEmpty();
		assertThat(Mapper.entitiesToGroupedResponses(List.of())).isEmpty();
	}

	@Test
	void entitiesToGroupedResponsesShouldHandleEntityWithNullWasteType() {
		final var entity = GarbageScheduleEntity.builder()
			.withStreet("Testgatan")
			.withHouseNumber("1")
			.withDriveSchedule("U11 TI2")
			.build();

		final var result = Mapper.entitiesToGroupedResponses(List.of(entity));

		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getSchedules()).isEmpty();
	}

}
