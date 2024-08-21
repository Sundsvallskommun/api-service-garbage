package se.sundsvall.garbage.service.scheduler;

import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import se.sundsvall.garbage.service.GarbageService;

@ExtendWith(MockitoExtension.class)
class UpdateGarbageSchedulesSchedulerTest {

	@Mock
	private GarbageService garbageServiceMock;

	@InjectMocks
	private UpdateGarbageSchedulesScheduler scheduler;

	@Test
	void execute() {
		// Arrange

		ReflectionTestUtils.setField(scheduler, "municipalityIds", List.of("2281"));
		final var municipalityId = "2281";

		// Act
		scheduler.execute();

		// Assert
		verify(garbageServiceMock).updateGarbageSchedules(municipalityId);
	}

}
