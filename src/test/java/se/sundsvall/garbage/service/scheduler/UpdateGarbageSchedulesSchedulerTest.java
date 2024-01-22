package se.sundsvall.garbage.service.scheduler;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.garbage.service.GarbageService;

@ExtendWith(MockitoExtension.class)
class UpdateGarbageSchedulesSchedulerTest {

	@Mock
	private GarbageService garbageServiceMock;

	@InjectMocks
	private UpdateGarbageSchedulesScheduler scheduler;

	@Test
	void execute() {
		// Act
		scheduler.execute();

		// Assert
		verify(garbageServiceMock).updateGarbageSchedules();
	}
}
