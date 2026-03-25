package se.sundsvall.garbage.service.mapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import se.sundsvall.garbage.api.model.Address;
import se.sundsvall.garbage.api.model.GarbageScheduleResponse;
import se.sundsvall.garbage.api.model.WasteSchedule;
import se.sundsvall.garbage.api.model.enums.FacilityCategory;
import se.sundsvall.garbage.api.model.enums.Week;
import se.sundsvall.garbage.api.model.enums.WeekDay;
import se.sundsvall.garbage.integration.db.entity.GarbageScheduleEntity;

public final class Mapper {

	private Mapper() {
		// Intentionally left blank
	}

	/**
	 * Maps a list of entities to grouped responses, where entities sharing the same address, drive schedule, and facility
	 * category are merged into a single response with multiple waste schedules.
	 *
	 * @param  entities the list of {@link GarbageScheduleEntity} to group and map
	 * @return          a list of grouped {@link GarbageScheduleResponse}, or an empty list if the input is {@code null} or
	 *                  empty
	 */
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

	/**
	 * Maps a single entity to a response with its waste schedule.
	 *
	 * @param  garbageScheduleEntity the {@link GarbageScheduleEntity} to map
	 * @return                       a {@link GarbageScheduleResponse}, or {@code null} if the input is {@code null}
	 */
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

	/**
	 * Maps the address fields from an entity to an {@link Address} object.
	 *
	 * @param  garbageScheduleEntity the entity to extract address fields from
	 * @return                       an {@link Address}, or {@code null} if the input is {@code null}
	 */
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

	/**
	 * Extracts the 3-character day-and-week suffix from a drive schedule string.
	 * <p>
	 * The suffix consists of a 2-character weekday code (e.g. "TI", "FR") followed by a week number ("1" for odd, "2" for
	 * even). For example, given "U11 TI2" this method returns "TI2".
	 * </p>
	 *
	 * @param  driveSchedule the drive schedule string (e.g. "U11 TI2", "KP50FR1"), or {@code null}
	 * @return               the 3-character day-week part, or {@code null} if the input is null, too short, or does not
	 *                       contain a valid weekday code
	 */
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

	/**
	 * Extracts the weekday from a drive schedule string.
	 *
	 * @param  driveSchedule the drive schedule string (e.g. "U11 TI2")
	 * @return               the {@link WeekDay}, or {@code null} if the input is {@code null} or unparseable
	 */
	private static WeekDay mapDayOfWeek(final String driveSchedule) {
		return Optional.ofNullable(extractDayWeekPart(driveSchedule))
			.map(part -> WeekDay.forValue(part.substring(0, 2)))
			.orElse(null);
	}

	/**
	 * Extracts the week parity (odd/even) from a drive schedule string.
	 * <p>
	 * A suffix ending in "1" maps to {@link Week#ODD}, anything else maps to {@link Week#EVEN}.
	 * </p>
	 *
	 * @param  driveSchedule the drive schedule string (e.g. "U11 TI2")
	 * @return               the {@link Week}, or {@code null} if the input is {@code null} or unparseable
	 */
	private static Week mapWeek(final String driveSchedule) {
		return Optional.ofNullable(extractDayWeekPart(driveSchedule))
			.map(part -> "1".equals(part.substring(2)) ? Week.ODD : Week.EVEN)
			.orElse(null);
	}

	private record GroupingKey(String street, String houseNumber, String postalCode, String city,
		String additionalInformation, String driveSchedule, FacilityCategory facilityCategory) {

		GroupingKey(final GarbageScheduleEntity entity) {
			this(entity.getStreet(), entity.getHouseNumber(), entity.getPostalCode(),
				entity.getCity(), entity.getAdditionalInformation(),
				entity.getDriveSchedule(), entity.getFacilityCategory());
		}
	}

}
