package se.sundsvall.garbage.service.mapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import se.sundsvall.garbage.api.model.Address;
import se.sundsvall.garbage.api.model.GarbageScheduleResponse;
import se.sundsvall.garbage.api.model.WasteSchedule;
import se.sundsvall.garbage.api.model.enums.Week;
import se.sundsvall.garbage.api.model.enums.WeekDay;
import se.sundsvall.garbage.integration.db.entity.GarbageScheduleEntity;

public final class Mapper {

	private Mapper() {
		// Intentionally left blank
	}

	public static List<GarbageScheduleResponse> entitiesToGroupedResponses(final List<GarbageScheduleEntity> entities) {
		if (entities == null || entities.isEmpty()) {
			return List.of();
		}

		final var grouped = new LinkedHashMap<GroupingKey, GarbageScheduleResponse>();

		for (final var entity : entities) {
			final var key = new GroupingKey(entity);
			grouped.computeIfAbsent(key, k -> GarbageScheduleResponse.builder()
				.withAddress(mapAddress(entity))
				.withAdditionalInformation(entity.getAdditionalInformation())
				.withGarbageScheduledDay(mapDayOfWeek(entity.getDriveSchedule()))
				.withGarbageScheduledWeek(mapWeek(entity.getDriveSchedule()))
				.withFacilityCategory(entity.getFacilityCategory())
				.withSchedules(new ArrayList<>())
				.build());

			Optional.ofNullable(entity.getWasteType())
				.ifPresent(wasteType -> grouped.get(key).getSchedules().add(
					WasteSchedule.builder()
						.withWasteType(wasteType.name())
						.withNextPickupDate(entity.getNextPickupDate())
						.build()));
		}

		return new ArrayList<>(grouped.values());
	}

	public static GarbageScheduleResponse entityToResponse(final GarbageScheduleEntity garbageScheduleEntity) {

		return Optional.ofNullable(garbageScheduleEntity)
			.map(entity -> GarbageScheduleResponse.builder()
				.withAddress(mapAddress(entity))
				.withAdditionalInformation(entity.getAdditionalInformation())
				.withGarbageScheduledDay(mapDayOfWeek(entity.getDriveSchedule()))
				.withGarbageScheduledWeek(mapWeek(entity.getDriveSchedule()))
				.withFacilityCategory(entity.getFacilityCategory())
				.withSchedules(Optional.ofNullable(entity.getWasteType())
					.map(wasteType -> List.of(WasteSchedule.builder()
						.withWasteType(wasteType.name())
						.withNextPickupDate(entity.getNextPickupDate())
						.build()))
					.orElse(List.of()))
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

	static String extractDayWeekPart(final String driveSchedule) {
		return Optional.ofNullable(driveSchedule)
			.map(String::trim)
			.filter(s -> s.length() >= 3)
			.map(s -> {
				final var spaceIndex = s.lastIndexOf(' ');
				if (spaceIndex >= 0) {
					return s.substring(spaceIndex + 1);
				}
				return s.substring(s.length() - 3);
			})
			.filter(s -> s.length() == 3)
			.filter(s -> WeekDay.forValue(s.substring(0, 2)) != null)
			.orElse(null);
	}

	private static WeekDay mapDayOfWeek(final String driveSchedule) {
		return Optional.ofNullable(extractDayWeekPart(driveSchedule))
			.map(part -> WeekDay.forValue(part.substring(0, 2)))
			.orElse(null);
	}

	private static Week mapWeek(final String driveSchedule) {
		return Optional.ofNullable(extractDayWeekPart(driveSchedule))
			.map(part -> "1".equals(part.substring(2)) ? Week.ODD : Week.EVEN)
			.orElse(null);
	}

	private record GroupingKey(String street, String houseNumber, String postalCode, String city,
		String additionalInformation, String driveSchedule, Object facilityCategory) {

		GroupingKey(final GarbageScheduleEntity entity) {
			this(entity.getStreet(), entity.getHouseNumber(), entity.getPostalCode(),
				entity.getCity(), entity.getAdditionalInformation(),
				entity.getDriveSchedule(), entity.getFacilityCategory());
		}
	}

}
