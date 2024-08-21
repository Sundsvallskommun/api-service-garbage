package se.sundsvall.garbage.service.scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import se.sundsvall.garbage.service.GarbageService;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Service
public class UpdateGarbageSchedulesScheduler {

	private final GarbageService garbageService;

	@Value("${schedulers.update-garbage-schedules.municipality-ids}")
	private List<String> municipalityIds;

	public UpdateGarbageSchedulesScheduler(final GarbageService garbageService) {
		this.garbageService = garbageService;
	}

	@Scheduled(cron = "${schedulers.update-garbage-schedules.cron}")
	@SchedulerLock(name = "updateGarbageSchedules", lockAtMostFor = "${schedulers.update-garbage-schedules.shedlock-lock-at-most-for}")
	public void execute() {
		municipalityIds.forEach(garbageService::updateGarbageSchedules);
	}

}
