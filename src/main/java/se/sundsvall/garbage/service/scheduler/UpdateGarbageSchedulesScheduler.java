package se.sundsvall.garbage.service.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import se.sundsvall.garbage.service.GarbageService;

@Service
public class UpdateGarbageSchedulesScheduler {

	private final GarbageService garbageService;

	public UpdateGarbageSchedulesScheduler(GarbageService garbageService) {
		this.garbageService = garbageService;
	}

	@Scheduled(cron = "${shedulers.update-garbage-schedules.cron}")
	@SchedulerLock(name = "updateGarbageSchedules", lockAtMostFor = "${shedulers.update-garbage-schedules.shedlock-lock-at-most-for}")
	public void execute() {
		garbageService.updateGarbageSchedules();
	}
}
