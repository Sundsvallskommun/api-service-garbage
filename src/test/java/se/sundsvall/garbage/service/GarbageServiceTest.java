package se.sundsvall.garbage.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.garbage.TestDataFactory.buildGarbageScheduleEntity;
import static se.sundsvall.garbage.TestDataFactory.buildGarbageScheduleRequest;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import se.sundsvall.garbage.api.model.enums.FacilityCategory;
import se.sundsvall.garbage.api.model.enums.Week;
import se.sundsvall.garbage.api.model.enums.WeekDay;
import se.sundsvall.garbage.integration.db.GarbageScheduleRepository;
import se.sundsvall.garbage.integration.db.entity.GarbageScheduleEntity;
import se.sundsvall.garbage.integration.db.specification.GarbageScheduleSpecification;
import se.sundsvall.garbage.integration.filehandler.FileHandler;

@ExtendWith(MockitoExtension.class)
class GarbageServiceTest {

	private final Page<GarbageScheduleEntity> garbageSchedulePage = new PageImpl<>(Collections.singletonList(buildGarbageScheduleEntity()));

	@Mock
	private Specification<GarbageScheduleEntity> mockSpecification;

	@Mock
	private GarbageScheduleRepository repository;

	@Mock
	private FileHandler fileHandler;

	@Mock
	private GarbageScheduleSpecification garbageScheduleSpecification;

	@InjectMocks
	private GarbageService service;

	@Test
	void getGarbageSchedule() {
		final var request = buildGarbageScheduleRequest();

		when(garbageScheduleSpecification.createGarbageScheduleSpecification(any())).thenReturn(mockSpecification);
		when(repository.findAll(ArgumentMatchers.<Specification<GarbageScheduleEntity>>any(), any(Pageable.class))).thenReturn(garbageSchedulePage);

		final var result = service.getGarbageSchedules(request);

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
		verify(repository).findAll(ArgumentMatchers.<Specification<GarbageScheduleEntity>>any(), any(Pageable.class));
		verifyNoMoreInteractions(repository);
	}

	@Test
	void updateGarbageSchedules() {

		service.updateGarbageSchedules();

		verify(fileHandler).downloadFile();
		verify(fileHandler).parseFile();
		verify(repository).deleteAllInBatch();
		verify(repository).saveAll(any());
		verifyNoMoreInteractions(fileHandler);
		verifyNoMoreInteractions(repository);

	}

}
