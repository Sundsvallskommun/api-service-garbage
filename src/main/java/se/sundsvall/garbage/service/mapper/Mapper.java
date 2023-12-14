package se.sundsvall.garbage.service.mapper;

import org.springframework.stereotype.Component;

import se.sundsvall.garbage.api.model.Address;
import se.sundsvall.garbage.api.model.GarbageScheduleResponse;
import se.sundsvall.garbage.api.model.GarbageScheduleResponse.Week;
import se.sundsvall.garbage.api.model.GarbageScheduleResponse.WeekDay;
import se.sundsvall.garbage.integration.db.entity.GarbageScheduleEntity;

@Component
public class Mapper {
	public GarbageScheduleResponse entityToResponse(GarbageScheduleEntity entity) {
		return GarbageScheduleResponse.builder()
			.withAddress(mapAddress(entity))
			.withAdditionalInformation(entity.getAdditionalInformation())
			.withGarbageScheduledDay(mapDayOfWeek(entity.getDriveSchedule()))
			.withGarbageScheduledWeek(mapWeek(entity.getDriveSchedule()))
			.withFacilityCategory(entity.getFacilityCategory())
			.build();
	}

	private Address mapAddress(GarbageScheduleEntity entity) {
		return Address.builder()
			.withStreet(entity.getStreet())
			.withHouseNumber(entity.getHouseNumber())
			.withPostalCode(entity.getPostalCode())
			.withCity(entity.getCity())
			.build();

	}

	private WeekDay mapDayOfWeek(String driveSchedule) {
		return WeekDay.forValue(driveSchedule.substring(4, 6));
	}

	private Week mapWeek(String driveSchedule) {
		return "1".equals(driveSchedule.substring(6)) ? Week.ODD : Week.EVEN;
	}

}
