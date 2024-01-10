package se.sundsvall.garbage.service.mapper;

import java.util.Optional;

import se.sundsvall.garbage.api.model.Address;
import se.sundsvall.garbage.api.model.GarbageScheduleResponse;
import se.sundsvall.garbage.api.model.enums.Week;
import se.sundsvall.garbage.api.model.enums.WeekDay;
import se.sundsvall.garbage.integration.db.entity.GarbageScheduleEntity;

public final class Mapper {

	private Mapper() {
		// Intentionally left blank
	}

	public static GarbageScheduleResponse entityToResponse(final GarbageScheduleEntity garbageScheduleEntity) {

		return Optional.ofNullable(garbageScheduleEntity)
			.map(entity -> GarbageScheduleResponse.builder()
				.withAddress(mapAddress(entity))
				.withAdditionalInformation(entity.getAdditionalInformation())
				.withGarbageScheduledDay(mapDayOfWeek(entity.getDriveSchedule()))
				.withGarbageScheduledWeek(mapWeek(entity.getDriveSchedule()))
				.withFacilityCategory(entity.getFacilityCategory())
				.build())
			.orElse(null);
	}

	private static Address mapAddress(final GarbageScheduleEntity garbageScheduleEntity) {
		return Optional.ofNullable(garbageScheduleEntity)
			.map(entity -> Address.builder()
				.withStreet(entity.getStreet())
				.withHouseNumber(entity.getHouseNumber())
				.withPostalCode(entity.getPostalCode())
				.withCity(entity.getCity())
				.build())
			.orElse(null);

	}

	private static WeekDay mapDayOfWeek(final String driveSchedule) {
		return Optional.ofNullable(driveSchedule)
			.filter(schedule -> schedule.length() >= 6)
			.map(schedule -> WeekDay.forValue(driveSchedule.substring(4, 6)))
			.orElse(null);
	}

	private static Week mapWeek(final String driveSchedule) {
		return Optional.ofNullable(driveSchedule)
			.filter(schedule -> driveSchedule.length() >= 6)
			.map(schedule -> "1".equals(schedule.substring(6)) ? Week.ODD : Week.EVEN)
			.orElse(null);
	}

}
