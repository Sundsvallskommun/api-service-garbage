package se.sundsvall.garbage.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.scheduling.health.Dept44HealthUtility;
import se.sundsvall.garbage.api.model.GarbageScheduleRequest;
import se.sundsvall.garbage.api.model.GarbageScheduleResponse;
import se.sundsvall.garbage.integration.db.GarbageScheduleRepository;
import se.sundsvall.garbage.integration.db.specification.GarbageScheduleSpecification;
import se.sundsvall.garbage.integration.filehandler.FileHandler;
import se.sundsvall.garbage.service.mapper.Mapper;

@Service
public class GarbageService {

	private static final Logger LOGGER = LoggerFactory.getLogger(GarbageService.class);

	// Sort by primary key so pagination is stable across calls. Uses the PK index
	// (no sort buffer) and gives full determinism for the in-memory grouping that follows.
	private static final Sort STABLE_SORT = Sort.by("id");

	private final GarbageScheduleRepository repository;

	private final FileHandler fileHandler;

	private final GarbageScheduleSpecification garbageScheduleSpecification;

	private final Dept44HealthUtility dept44HealthUtility;

	@Value("${schedulers.update-garbage-schedules.name}")
	private String scheduledJobName;

	public GarbageService(final GarbageScheduleRepository repository, final FileHandler fileHandler, final GarbageScheduleSpecification garbageScheduleSpecification, final Dept44HealthUtility dept44HealthUtility) {
		this.repository = repository;
		this.fileHandler = fileHandler;
		this.garbageScheduleSpecification = garbageScheduleSpecification;
		this.dept44HealthUtility = dept44HealthUtility;
	}

	private static List<GarbageScheduleResponse> paginate(final List<GarbageScheduleResponse> grouped, final GarbageScheduleRequest request) {
		return Optional.ofNullable(request.getLimit())
			.map(limit -> {
				final var page = Optional.ofNullable(request.getPage()).orElse(1);
				final var from = Math.min((page - 1) * limit, grouped.size());
				final var to = Math.min(from + limit, grouped.size());
				return (List<GarbageScheduleResponse>) new ArrayList<>(grouped.subList(from, to));
			})
			.orElse(grouped);
	}

	public List<GarbageScheduleResponse> getGarbageSchedules(final String municipalityId, final GarbageScheduleRequest request) {
		// Pagination must be applied to grouped responses, not to entity rows. The DB stores
		// one row per (address, wasteType); paging the rows splits an address across pages and
		// produces partial schedules, so we load matching entities, group, and then slice.
		final var entities = repository.findAll(
			garbageScheduleSpecification.createGarbageScheduleSpecification(request, municipalityId),
			STABLE_SORT);
		return paginate(Mapper.entitiesToGroupedResponses(entities), request);
	}

	@Async
	@Transactional
	public void updateGarbageSchedulesAsynchronously(final String municipalityId) {
		performUpdate(municipalityId);
	}

	@Transactional
	public void updateGarbageSchedules(final String municipalityId) {
		performUpdate(municipalityId);
	}

	private void performUpdate(final String municipalityId) {
		try {
			LOGGER.info("Start updating schedules");
			fileHandler.downloadFile();
			final var entities = fileHandler.parseFile();
			if (entities.isEmpty()) {
				dept44HealthUtility.setHealthIndicatorUnhealthy(scheduledJobName, "Schedule file did not contain any rows");
				return;
			}
			entities.forEach(entity -> entity.setMunicipalityId(municipalityId));
			LOGGER.info("Replacing {} existing entries in database with {} entries", repository.count(), entities.size());
			repository.deleteAllInBatch();
			repository.saveAll(entities);
		} catch (final Exception e) {
			dept44HealthUtility.setHealthIndicatorUnhealthy(scheduledJobName, "Could not complete update of garbage schedules");
			LOGGER.info("Exception occurred when updating schedules", e);
		} finally {
			LOGGER.info("End updating schedules");
		}
	}

}
