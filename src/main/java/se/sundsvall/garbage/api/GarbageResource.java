package se.sundsvall.garbage.api;

import java.util.List;

import javax.validation.Valid;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.zalando.problem.Problem;

import se.sundsvall.garbage.api.model.GarbageScheduleRequest;
import se.sundsvall.garbage.api.model.GarbageScheduleResponse;
import se.sundsvall.garbage.service.GarbageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/schedules", produces = MediaType.APPLICATION_JSON_VALUE)
@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class)))
@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class)))
@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = Problem.class)))
@Tag(name = "GarbageSchedule")
public class GarbageResource {
    
    private final GarbageService garbageService;
    
    public GarbageResource(GarbageService garbageService) {
        this.garbageService = garbageService;
    }
    
    @GetMapping
    @Operation(summary = "Get garbage schedule for an address")
    @ApiResponse(responseCode = "200", description = "Successful Operation", content = @Content(schema = @Schema(implementation = GarbageScheduleResponse.class)))
    public ResponseEntity<List<GarbageScheduleResponse>> getGarbage(@ParameterObject @Valid GarbageScheduleRequest request) {
        return ResponseEntity.ok(garbageService.getGarbageSchedules(request));
    }
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Populate the garbage schedule database. Temporary endpoint. Probably.")
    @ApiResponse(responseCode = "200", description = "Successful Operation", content = @Content(schema = @Schema()))
    public ResponseEntity<Void> updateGarbageScheduleDb(@RequestParam("file") MultipartFile file) {
        garbageService.updateGarbageSchedules(file);
        return ResponseEntity.ok().build();
        
        
    }
}
