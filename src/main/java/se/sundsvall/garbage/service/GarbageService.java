package se.sundsvall.garbage.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se.sundsvall.garbage.api.model.GarbageScheduleRequest;
import se.sundsvall.garbage.api.model.GarbageScheduleResponse;
import se.sundsvall.garbage.integration.db.GarbageScheduleRepository;
import se.sundsvall.garbage.integration.db.specification.GarbageScheduleSpecification;
import se.sundsvall.garbage.integration.filehandler.FileHandler;
import se.sundsvall.garbage.service.mapper.Mapper;

@Service
public class GarbageService {
	private static final Logger LOGGER = LoggerFactory.getLogger(GarbageService.class);

	private final GarbageScheduleRepository repository;

	private final FileHandler fileHandler;

	private final GarbageScheduleSpecification garbageScheduleSpecification;

	public GarbageService(final GarbageScheduleRepository repository, final FileHandler fileHandler, final GarbageScheduleSpecification garbageScheduleSpecification) {
		this.repository = repository;
		this.fileHandler = fileHandler;
		this.garbageScheduleSpecification = garbageScheduleSpecification;
	}

	public List<GarbageScheduleResponse> getGarbageSchedules(final String municipalityId, final GarbageScheduleRequest request) {
		return repository.findAll(garbageScheduleSpecification.createGarbageScheduleSpecification(request, municipalityId), getPagingParameters(request))
			.stream()
			.map(Mapper::entityToResponse)
			.toList();
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
			entities.forEach(entity -> entity.setMunicipalityId(municipalityId));
			LOGGER.info("Replacing {} existing entries in database with {} entries", repository.count(), entities.size());
			repository.deleteAllInBatch();
			repository.saveAll(entities);
		} catch (final Exception e) {
			LOGGER.info("Exception occurred when updating schedules", e);
		} finally {
			LOGGER.info("End updating schedules");
		}
	}

	private Pageable getPagingParameters(final GarbageScheduleRequest request) {
		return PageRequest.of(request.getPage() - 1, request.getLimit());
	}
}
