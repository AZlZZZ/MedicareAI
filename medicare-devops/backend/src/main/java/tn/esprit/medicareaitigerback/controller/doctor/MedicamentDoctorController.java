package tn.esprit.medicareaitigerback.controller.doctor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;
import tn.esprit.medicareaitigerback.models.dto.request.CreateMedicamentDTO;
import tn.esprit.medicareaitigerback.models.dto.request.CreatePatientMedicamentScheduleDTO;
import tn.esprit.medicareaitigerback.models.dto.request.UpdateMedicamentDTO;
import tn.esprit.medicareaitigerback.models.dto.response.ApiRespDTO;
import tn.esprit.medicareaitigerback.models.dto.response.ClientRespDTO;
import tn.esprit.medicareaitigerback.models.dto.response.MedicamentResponseDTO;
import tn.esprit.medicareaitigerback.models.dto.response.PatientMedicamentScheduleResponseDTO;
import tn.esprit.medicareaitigerback.models.enumeration.UserType;
import tn.esprit.medicareaitigerback.security.UserDetailsImpl;
import tn.esprit.medicareaitigerback.services.interfaces.IMedicamentService;
import tn.esprit.medicareaitigerback.services.interfaces.IPatientMedicamentScheduleService;
import tn.esprit.medicareaitigerback.services.interfaces.IPatientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/doctor/medicaments")
@AllArgsConstructor
@CrossOrigin
@Tag(name = "201.doctor.medicaments", description = "Doctor/agent medicament + schedule management")
public class MedicamentDoctorController {

    private final IMedicamentService medicamentService;
    private final IPatientMedicamentScheduleService scheduleService;
    private final IPatientService patientService;
    private static final Logger logger = LoggerFactory.getLogger(MedicamentDoctorController.class);
    private static final String SUCCESS = "success";

    private void assertDoctorOrAgent() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth != null ? auth.getPrincipal() : null;
        if (!(principal instanceof UserDetailsImpl userDetails) ||
                (userDetails.getUserType() != UserType.DOCTOR && userDetails.getUserType() != UserType.AGENT)) {
            throw new AccessDeniedException("Only doctors or agents can access this endpoint");
        }
    }

    @Operation(summary = "Get all patients (for doctor to pick a patient)")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "/patients")
    public ResponseEntity<ApiRespDTO<Page<ClientRespDTO>>> getPatients(
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int numberPerPage,
            @RequestParam(defaultValue = "[]") String queryValue,
            @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection,
            @RequestParam(defaultValue = "id") String sortProperty
    ) {
        assertDoctorOrAgent();
        return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS,
                patientService.findAllClient(pageIndex, numberPerPage, queryValue, sortDirection, sortProperty)));
    }

    @Operation(summary = "Create a medicament")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = SUCCESS, content = @Content(schema = @Schema(implementation = MedicamentResponseDTO.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "/create")
    public ResponseEntity<ApiRespDTO<MedicamentResponseDTO>> create(@RequestBody @Valid CreateMedicamentDTO dto) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            assertDoctorOrAgent();
            logger.info("Received API request to /api/v1/doctor/medicaments/create");
            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, medicamentService.create(dto)));
        } finally {
            stopWatch.stop();
            logger.info("API /api/v1/doctor/medicaments/create executed in {} ms", stopWatch.getTotalTimeMillis());
        }
    }

    @Operation(summary = "Get all medicaments")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "/getAll")
    public ResponseEntity<ApiRespDTO<List<MedicamentResponseDTO>>> getAll() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            assertDoctorOrAgent();
            logger.info("Received API request to /api/v1/doctor/medicaments/getAll");
            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, medicamentService.findAll()));
        } finally {
            stopWatch.stop();
            logger.info("API /api/v1/doctor/medicaments/getAll executed in {} ms", stopWatch.getTotalTimeMillis());
        }
    }

    @Operation(summary = "Edit medicament")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping(value = "/{id}")
    public ResponseEntity<ApiRespDTO<MedicamentResponseDTO>> edit(@PathVariable("id") Long id, @RequestBody @Valid UpdateMedicamentDTO dto) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            assertDoctorOrAgent();
            logger.info("Received API request to /api/v1/doctor/medicaments/{}/edit", id);
            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, medicamentService.edit(id, dto)));
        } finally {
            stopWatch.stop();
            logger.info("API /api/v1/doctor/medicaments/{}/edit executed in {} ms", id, stopWatch.getTotalTimeMillis());
        }
    }

    @Operation(summary = "Update stock of a medicament (agent only)")
    @SecurityRequirement(name = "Bearer Authentication")
    @PatchMapping(value = "/{id}/stock")
    public ResponseEntity<ApiRespDTO<MedicamentResponseDTO>> updateStock(
            @PathVariable("id") Long id,
            @RequestParam("quantity") Integer quantity) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Object principal = auth != null ? auth.getPrincipal() : null;
            if (!(principal instanceof UserDetailsImpl userDetails) || userDetails.getUserType() != UserType.AGENT) {
                throw new AccessDeniedException("Only agents can update stock");
            }
            logger.info("Received API request to PATCH /api/v1/doctor/medicaments/{}/stock", id);
            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, medicamentService.updateStock(id, quantity)));
        } finally {
            stopWatch.stop();
            logger.info("API updateStock executed in {} ms", stopWatch.getTotalTimeMillis());
        }
    }

    @Operation(summary = "Delete medicament")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ApiRespDTO<Void>> delete(@PathVariable("id") Long id) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            assertDoctorOrAgent();
            logger.info("Received API request to /api/v1/doctor/medicaments/{}/delete", id);
            medicamentService.delete(id);
            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, null));
        } finally {
            stopWatch.stop();
            logger.info("API /api/v1/doctor/medicaments/{}/delete executed in {} ms", id, stopWatch.getTotalTimeMillis());
        }
    }

    @Operation(summary = "Get all active medicament schedules for a patient")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "/patients/{patientId}/medicament-schedules")
    public ResponseEntity<ApiRespDTO<List<PatientMedicamentScheduleResponseDTO>>> getSchedules(
            @PathVariable("patientId") Long patientId
    ) {
        assertDoctorOrAgent();
        return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, scheduleService.findByPatient(patientId)));
    }

    @Operation(summary = "Doctor creates/assigns a medicament schedule to a patient")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "/patients/{patientId}/medicament-schedules")
    public ResponseEntity<ApiRespDTO<PatientMedicamentScheduleResponseDTO>> createSchedule(
            @PathVariable("patientId") Long patientId,
            @RequestBody @Valid CreatePatientMedicamentScheduleDTO dto
    ) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            assertDoctorOrAgent();
            logger.info("Received API request to /api/v1/doctor/medicaments/patients/{}/medicament-schedules", patientId);
            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, scheduleService.create(patientId, dto)));
        } finally {
            stopWatch.stop();
            logger.info("API createSchedule executed in {} ms", stopWatch.getTotalTimeMillis());
        }
    }

    @Operation(summary = "Doctor edits a medicament schedule")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping(value = "/medicament-schedules/{scheduleId}")
    public ResponseEntity<ApiRespDTO<PatientMedicamentScheduleResponseDTO>> editSchedule(
            @PathVariable("scheduleId") Long scheduleId,
            @RequestBody @Valid CreatePatientMedicamentScheduleDTO dto
    ) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            assertDoctorOrAgent();
            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, scheduleService.edit(scheduleId, dto)));
        } finally {
            stopWatch.stop();
            logger.info("API editSchedule executed in {} ms", stopWatch.getTotalTimeMillis());
        }
    }

    @Operation(summary = "Doctor deletes a medicament schedule")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping(value = "/medicament-schedules/{scheduleId}")
    public ResponseEntity<ApiRespDTO<Void>> deleteSchedule(@PathVariable("scheduleId") Long scheduleId) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            assertDoctorOrAgent();
            scheduleService.delete(scheduleId);
            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, null));
        } finally {
            stopWatch.stop();
            logger.info("API deleteSchedule executed in {} ms", stopWatch.getTotalTimeMillis());
        }
    }
}

