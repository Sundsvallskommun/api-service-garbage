package se.sundsvall.garbage.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import se.sundsvall.garbage.api.model.GarbageScheduleRequest;
import se.sundsvall.garbage.api.model.GarbageScheduleResponse;
import se.sundsvall.garbage.integration.db.GarbageScheduleRepository;
import se.sundsvall.garbage.integration.db.specification.GarbageScheduleSpecification;
import se.sundsvall.garbage.integration.filehandler.FileHandler;
import se.sundsvall.garbage.service.mapper.Mapper;

@Service
@EnableScheduling
public class GarbageService {
    
    private final GarbageScheduleRepository repository;
    private final Mapper mapper;
    private final FileHandler fileHandler;
    private final GarbageScheduleSpecification garbageScheduleSpecification;
    
    
    public GarbageService(GarbageScheduleRepository repository, Mapper mapper, FileHandler fileHandler, GarbageScheduleSpecification garbageScheduleSpecification) {
        this.repository = repository;
        this.mapper = mapper;
        this.fileHandler = fileHandler;
        this.garbageScheduleSpecification = garbageScheduleSpecification;
    }
    
    public List<GarbageScheduleResponse> getGarbageSchedules(GarbageScheduleRequest request) {
        return repository.findAll(garbageScheduleSpecification.createGarbageScheduleSpecification(request), getPagingParameters(request))
            .stream()
            .map(mapper::entityToResponse)
            .toList();
    }
    
    @Scheduled(cron = "0 0 5 * * MON-FRI")
    public void updateGarbageSchedules() {
        fileHandler.downloadFile();
        repository.deleteAllInBatch();
        repository.saveAll(fileHandler.parseFile());
        
    }
    
    private Pageable getPagingParameters(GarbageScheduleRequest request) {
        return PageRequest.of(request.getPage() - 1, request.getLimit());
    }
    
}
