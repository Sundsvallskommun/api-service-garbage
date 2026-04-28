package se.sundsvall.garbage.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.scheduling.health.Dept44HealthUtility;
import se.sundsvall.garbage.api.model.GarbageScheduleRequest;
import se.sundsvall.garbage.api.model.enums.FacilityCategory;
import se.sundsvall.garbage.api.model.enums.WasteType;
import se.sundsvall.garbage.api.model.enums.Week;
import se.sundsvall.garbage.api.model.enums.WeekDay;
import se.sundsvall.garbage.integration.db.GarbageScheduleRepository;
import se.sundsvall.garbage.integration.db.entity.GarbageScheduleEntity;
import se.sundsvall.garbage.integration.db.specification.GarbageScheduleSpecification;
import se.sundsvall.garbage.integration.filehandler.FileHandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.garbage.TestDataFactory.buildGarbageScheduleEntity;
import static se.sundsvall.garbage.TestDataFactory.buildGarbageScheduleRequest;

@ExtendWith(MockitoExtension.class)
class GarbageServiceTest {

	@Mock
	private Specification<GarbageScheduleEntity> mockSpecification;

	@Mock
	private Dept44HealthUtility dept44HealthUtility;

	@Mock
	private GarbageScheduleRepository repository;

	@Mock
	private FileHandler fileHandler;

	@Mock
	private GarbageScheduleSpecification garbageScheduleSpecification;

	@InjectMocks
	private GarbageService service;

	@BeforeEach
	void setUp() throws NoSuchFieldException, IllegalAccessException {

		final var field = GarbageService.class.getDeclaredField("scheduledJobName");
		field.setAccessible(true);
		field.set(service, "scheduledJobName");
	}

	@Test
	void getGarbageSchedule() {

		// Arrange
		final var request = buildGarbageScheduleRequest();
		final var municipalityId = "2281";

		when(garbageScheduleSpecification.createGarbageScheduleSpecification(any(), eq(municipalityId))).thenReturn(mockSpecification);
		when(repository.findAll(ArgumentMatchers.<Specification<GarbageScheduleEntity>>any(), any(Sort.class)))
			.thenReturn(List.of(buildGarbageScheduleEntity()));

		// Act
		final var result = service.getGarbageSchedules(municipalityId, request);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getFirst().getGarbageScheduledDay()).isEqualTo(WeekDay.FRIDAY);
		assertThat(result.getFirst().getGarbageScheduledWeek()).isEqualTo(Week.ODD);
		assertThat(result.getFirst().getAdditionalInformation()).isEqualTo(request.getAdditionalInformation());
		assertThat(result.getFirst().getAddress().getPostalCode()).isEqualTo(request.getPostalCode());
		assertThat(result.getFirst().getAddress().getStreet()).isEqualTo(request.getStreet());
		assertThat(result.getFirst().getAddress().getHouseNumber()).isEqualTo(request.getHouseNumber());
		assertThat(result.getFirst().getAddress().getCity()).isEqualTo(request.getCity());
		assertThat(result.getFirst().getFacilityCategory()).isEqualTo(FacilityCategory.VILLA);

		verifyNoInteractions(fileHandler);
		verify(repository).findAll(ArgumentMatchers.<Specification<GarbageScheduleEntity>>any(), any(Sort.class));
		verifyNoMoreInteractions(repository);
	}

	@Test
	void getGarbageSchedule_paginatesGroupedResponsesNotEntityRows() {
		// Two distinct addresses, each with two waste-type rows. With limit=1 and page=2, the
		// caller must receive the second address with BOTH its schedules — not a partial slice.

		// Arrange
		final var municipalityId = "2281";
		final var addressOneWaste = entity("Storgatan", "1", WasteType.WASTE, LocalDate.of(2026, 5, 1));
		final var addressOneFood = entity("Storgatan", "1", WasteType.FOOD, LocalDate.of(2026, 5, 1));
		final var addressTwoWaste = entity("Storgatan", "2", WasteType.WASTE, LocalDate.of(2026, 5, 2));
		final var addressTwoFood = entity("Storgatan", "2", WasteType.FOOD, LocalDate.of(2026, 5, 2));

		final var request = GarbageScheduleRequest.builder().build();
		request.setPage(2);
		request.setLimit(1);

		when(garbageScheduleSpecification.createGarbageScheduleSpecification(any(), eq(municipalityId))).thenReturn(mockSpecification);
		when(repository.findAll(ArgumentMatchers.<Specification<GarbageScheduleEntity>>any(), any(Sort.class)))
			.thenReturn(List.of(addressOneWaste, addressOneFood, addressTwoWaste, addressTwoFood));

		// Act
		final var result = service.getGarbageSchedules(municipalityId, request);

		// Assert
		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getAddress().getHouseNumber()).isEqualTo("2");
		assertThat(result.getFirst().getSchedules())
			.extracting("wasteType")
			.containsExactlyInAnyOrder("WASTE", "FOOD");
	}

	@Test
	void getGarbageSchedule_returnsAllWhenLimitIsNull() {
		// Arrange
		final var municipalityId = "2281";
		final var request = GarbageScheduleRequest.builder().build();
		// No page/limit set → unpaged

		when(garbageScheduleSpecification.createGarbageScheduleSpecification(any(), eq(municipalityId))).thenReturn(mockSpecification);
		when(repository.findAll(ArgumentMatchers.<Specification<GarbageScheduleEntity>>any(), any(Sort.class)))
			.thenReturn(List.of(
				entity("Storgatan", "1", WasteType.WASTE, LocalDate.of(2026, 5, 1)),
				entity("Storgatan", "2", WasteType.WASTE, LocalDate.of(2026, 5, 2))));

		// Act
		final var result = service.getGarbageSchedules(municipalityId, request);

		// Assert
		assertThat(result).hasSize(2);
	}

	@Test
	void getGarbageSchedule_pageBeyondEndReturnsEmpty() {
		// Arrange
		final var municipalityId = "2281";
		final var request = GarbageScheduleRequest.builder().build();
		request.setPage(5);
		request.setLimit(10);

		when(garbageScheduleSpecification.createGarbageScheduleSpecification(any(), eq(municipalityId))).thenReturn(mockSpecification);
		when(repository.findAll(ArgumentMatchers.<Specification<GarbageScheduleEntity>>any(), any(Sort.class)))
			.thenReturn(List.of(entity("Storgatan", "1", WasteType.WASTE, LocalDate.of(2026, 5, 1))));

		// Act
		final var result = service.getGarbageSchedules(municipalityId, request);

		// Assert
		assertThat(result).isEmpty();
	}

	private static GarbageScheduleEntity entity(final String street, final String houseNumber, final WasteType wasteType, final LocalDate nextPickupDate) {
		return GarbageScheduleEntity.builder()
			.withStreet(street)
			.withHouseNumber(houseNumber)
			.withPostalCode("85731")
			.withCity("Sundsvall")
			.withDriveSchedule("KP10FR1")
			.withFacilityCategory(FacilityCategory.VILLA)
			.withWasteType(wasteType)
			.withNextPickupDate(nextPickupDate)
			.build();
	}

	@Test
	void updateGarbageSchedulesAsynchronously() {
		// Arrange
		final var municipalityId = "2281";
		when(fileHandler.parseFile()).thenReturn(List.of(GarbageScheduleEntity.builder().build()));

		// Act
		service.updateGarbageSchedulesAsynchronously(municipalityId);

		// Assert
		verify(fileHandler).downloadFile();
		verify(fileHandler).parseFile();
		verify(repository).count();
		verify(repository).deleteAllInBatch();
		verify(repository).saveAll(any());
		verifyNoMoreInteractions(fileHandler);
		verifyNoMoreInteractions(repository);
	}

	@Test
	void updateGarbageSchedules() {
		// Arrange
		final var municipalityId = "2281";
		when(fileHandler.parseFile()).thenReturn(List.of(GarbageScheduleEntity.builder().build()));

		// Act
		service.updateGarbageSchedules(municipalityId);

		// Assert
		verify(fileHandler).downloadFile();
		verify(fileHandler).parseFile();
		verify(repository).count();
		verify(repository).deleteAllInBatch();
		verify(repository).saveAll(any());
		verifyNoMoreInteractions(fileHandler);
		verifyNoMoreInteractions(repository);
	}

	@Test
	void verifyAnnotations() {
		final var methods = List.of(service.getClass().getDeclaredMethods());

		assertThat(
			methods.stream()
				.filter(m -> "updateGarbageSchedules".equals(m.getName()))
				.map(Method::getDeclaredAnnotations)
				.map(List::of)
				.flatMap(List::stream)
				.map(Annotation::annotationType)
				.toArray())
			.containsExactlyInAnyOrder(Transactional.class);

		assertThat(
			methods.stream()
				.filter(m -> "updateGarbageSchedulesAsynchronously".equals(m.getName()))
				.map(Method::getDeclaredAnnotations)
				.map(List::of)
				.flatMap(List::stream)
				.map(Annotation::annotationType)
				.toArray())
			.containsExactlyInAnyOrder(Transactional.class, Async.class);
	}

	@Test
	void updateGarbageSchedulesAsynchronouslyExceptionThrown() {
		// Arrange
		final var municipalityId = "2281";
		when(fileHandler.parseFile()).thenThrow(new RuntimeException("Test exception"));

		// Act
		service.updateGarbageSchedulesAsynchronously(municipalityId);

		// Assert
		verify(fileHandler).downloadFile();
		verify(fileHandler).parseFile();
		verify(dept44HealthUtility).setHealthIndicatorUnhealthy("scheduledJobName", "Could not complete update of garbage schedules");
		verifyNoMoreInteractions(fileHandler);
		verifyNoMoreInteractions(repository);
	}

	@Test
	void updateGarbageSchedulesAsynchronouslyEntitiesEmpty() {
		// Arrange
		final var municipalityId = "2281";
		when(fileHandler.parseFile()).thenReturn(Collections.emptyList());

		// Act
		service.updateGarbageSchedulesAsynchronously(municipalityId);

		// Assert
		verify(fileHandler).downloadFile();
		verify(fileHandler).parseFile();
		verify(dept44HealthUtility).setHealthIndicatorUnhealthy("scheduledJobName", "Schedule file did not contain any rows");
		verifyNoMoreInteractions(fileHandler);
		verifyNoMoreInteractions(repository);
	}

}
