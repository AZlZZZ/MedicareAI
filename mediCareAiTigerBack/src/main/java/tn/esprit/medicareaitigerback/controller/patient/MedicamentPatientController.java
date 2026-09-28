package tn.esprit.medicareaitigerback.controller.patient;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;
import tn.esprit.medicareaitigerback.models.dto.response.ApiRespDTO;
import tn.esprit.medicareaitigerback.models.dto.response.MedicationRecommendationResponseDTO;
import tn.esprit.medicareaitigerback.models.dto.response.PatientMedicamentAlertResponseDTO;
import tn.esprit.medicareaitigerback.models.dto.response.PatientMedicamentScheduleResponseDTO;
import tn.esprit.medicareaitigerback.models.enumeration.UserType;
import tn.esprit.medicareaitigerback.security.UserDetailsImpl;
import tn.esprit.medicareaitigerback.services.interfaces.IPatientMedicamentAlertService;
import tn.esprit.medicareaitigerback.services.interfaces.IPatientMedicamentScheduleService;
import tn.esprit.medicareaitigerback.services.interfaces.IRecommendationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "api/v1/client/medicaments/alerts")
@AllArgsConstructor
@CrossOrigin
@Tag(name = "205.CLIENT.medicaments", description = "Patient medicament alerts")
public class MedicamentPatientController {

    private final IPatientMedicamentAlertService alertService;
    private final IPatientMedicamentScheduleService scheduleService;
    private final IRecommendationService recommendationService;
    private static final Logger logger = LoggerFactory.getLogger(MedicamentPatientController.class);
    private static final String SUCCESS = "success";

    private Long currentPatientId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth != null ? auth.getPrincipal() : null;
        if (!(principal instanceof UserDetailsImpl userDetails) || userDetails.getUserType() != UserType.PATIENT) {
            throw new AccessDeniedException("Only patients can access this endpoint");
        }
        return userDetails.getId();
    }

    @Operation(summary = "Get upcoming medicament alerts")
    @ApiResponse(responseCode = "200", description = SUCCESS, content = @Content(schema = @Schema(implementation = PatientMedicamentAlertResponseDTO.class)))
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "/upcoming")
    public ResponseEntity<ApiRespDTO<List<PatientMedicamentAlertResponseDTO>>> upcoming(
            @RequestParam(name = "weeks", defaultValue = "4", required = false) int weeks
    ) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            Long patientId = currentPatientId();
            logger.info("Received API request to /api/v1/client/medicaments/alerts/upcoming weeks={}", weeks);
            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, alertService.getUpcomingAlerts(patientId, weeks)));
        } finally {
            stopWatch.stop();
            logger.info("API /api/v1/client/medicaments/alerts/upcoming executed in {} ms", stopWatch.getTotalTimeMillis());
        }
    }

    @Operation(summary = "Get patient medicament schedules")
    @ApiResponse(responseCode = "200", description = SUCCESS, content = @Content(schema = @Schema(implementation = PatientMedicamentScheduleResponseDTO.class)))
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "/schedules")
    public ResponseEntity<ApiRespDTO<List<PatientMedicamentScheduleResponseDTO>>> getSchedules() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            Long patientId = currentPatientId();
            logger.info("Received API request to /api/v1/client/medicaments/alerts/schedules");
            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, scheduleService.findByPatient(patientId)));
        } finally {
            stopWatch.stop();
            logger.info("API /api/v1/client/medicaments/alerts/schedules executed in {} ms", stopWatch.getTotalTimeMillis());
        }
    }

    @Operation(summary = "Mark medicament alert as read")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "/{alertId}/read")
    public ResponseEntity<ApiRespDTO<Void>> markRead(
            @PathVariable("alertId") Long alertId
    ) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            Long patientId = currentPatientId();
            alertService.markAsRead(patientId, alertId);
            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, null));
        } finally {
            stopWatch.stop();
            logger.info("API /api/v1/client/medicaments/alerts/{}/read executed in {} ms", alertId, stopWatch.getTotalTimeMillis());
        }
    }

    @Operation(summary = "Get medication recommendations for current patient")
    @ApiResponse(responseCode = "200", description = SUCCESS, content = @Content(schema = @Schema(implementation = MedicationRecommendationResponseDTO.class)))
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "/recommendations")
    public ResponseEntity<ApiRespDTO<List<MedicationRecommendationResponseDTO>>> recommendations(
            @RequestParam(name = "topK", defaultValue = "5", required = false) int topK
    ) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            Long patientId = currentPatientId();
            logger.info("Received API request to /api/v1/client/medicaments/alerts/recommendations topK={}", topK);
            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, recommendationService.recommendForPatient(patientId, topK)));
        } finally {
            stopWatch.stop();
            logger.info("API /api/v1/client/medicaments/alerts/recommendations executed in {} ms", stopWatch.getTotalTimeMillis());
        }
    }

    @Operation(summary = "Get disease recommendations by symptoms using Python API")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "/recommend-by-symptoms")
    public ResponseEntity<ApiRespDTO<Map<String, Object>>> recommendBySymptoms(
            @RequestParam(name = "symptoms") String symptoms
    ) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            currentPatientId();
            logger.info("Received API request to /api/v1/client/medicaments/alerts/recommend-by-symptoms symptoms={}", symptoms);
            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, recommendationService.recommendBySymptoms(symptoms)));
        } finally {
            stopWatch.stop();
            logger.info("API /api/v1/client/medicaments/alerts/recommend-by-symptoms executed in {} ms", stopWatch.getTotalTimeMillis());
        }
    }
}

