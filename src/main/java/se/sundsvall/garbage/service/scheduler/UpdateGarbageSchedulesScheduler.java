package se.sundsvall.garbage.service.scheduler;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.sundsvall.dept44.scheduling.Dept44Scheduled;
import se.sundsvall.garbage.service.GarbageService;

@Service
public class UpdateGarbageSchedulesScheduler {

	private final GarbageService garbageService;

	@Value("${schedulers.update-garbage-schedules.municipality-ids}")
	private List<String> municipalityIds;

	public UpdateGarbageSchedulesScheduler(final GarbageService garbageService) {
		this.garbageService = garbageService;
	}

	@Dept44Scheduled(
		cron = "${schedulers.update-garbage-schedules.cron}",
		name = "${schedulers.update-garbage-schedules.name}",
		lockAtMostFor = "${schedulers.update-garbage-schedules.shedlock-lock-at-most-for}",
		maximumExecutionTime = "${schedulers.update-garbage-schedules.maximum-execution-time}")
	public void execute() {
		municipalityIds.forEach(garbageService::updateGarbageSchedules);
	}

}
