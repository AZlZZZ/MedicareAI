package tn.esprit.medicareaitigerback.controller.patient;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import tn.esprit.medicareaitigerback.models.dto.response.ApiRespDTO;
import tn.esprit.medicareaitigerback.models.dto.response.MedicationRecommendationResponseDTO;
import tn.esprit.medicareaitigerback.models.enumeration.UserState;
import tn.esprit.medicareaitigerback.models.enumeration.UserType;
import tn.esprit.medicareaitigerback.security.UserDetailsImpl;
import tn.esprit.medicareaitigerback.services.interfaces.IPatientMedicamentAlertService;
import tn.esprit.medicareaitigerback.services.interfaces.IPatientMedicamentScheduleService;
import tn.esprit.medicareaitigerback.services.interfaces.IRecommendationService;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MedicamentPatientControllerTest {

    @AfterEach
    void cleanUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void recommendationsReturnsData() {
        IPatientMedicamentAlertService alertService = mock(IPatientMedicamentAlertService.class);
        IPatientMedicamentScheduleService scheduleService = mock(IPatientMedicamentScheduleService.class);
        IRecommendationService recommendationService = mock(IRecommendationService.class);

        MedicamentPatientController controller = new MedicamentPatientController(
                alertService,
                scheduleService,
                recommendationService
        );

        UserDetailsImpl principal = new UserDetailsImpl(
                42L,
                "patient@test.com",
                "pass",
                Collections.emptyList(),
                UserType.PATIENT,
                UserState.ENABLED
        );
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );

        when(recommendationService.recommendForPatient(eq(42L), anyInt())).thenReturn(List.of(
                new MedicationRecommendationResponseDTO(1L, "Paracetamol", 0.91, "model_score", "python-model")
        ));

        ResponseEntity<ApiRespDTO<List<MedicationRecommendationResponseDTO>>> response = controller.recommendations(5);
        assertEquals(200, response.getBody().getCode());
        assertEquals(1L, response.getBody().getData().get(0).getMedicamentId());
        assertEquals("Paracetamol", response.getBody().getData().get(0).getMedicamentName());
    }

    @Test
    void recommendBySymptomsReturnsPythonPayload() {
        IPatientMedicamentAlertService alertService = mock(IPatientMedicamentAlertService.class);
        IPatientMedicamentScheduleService scheduleService = mock(IPatientMedicamentScheduleService.class);
        IRecommendationService recommendationService = mock(IRecommendationService.class);

        MedicamentPatientController controller = new MedicamentPatientController(
                alertService,
                scheduleService,
                recommendationService
        );

        UserDetailsImpl principal = new UserDetailsImpl(
                77L,
                "patient2@test.com",
                "pass",
                Collections.emptyList(),
                UserType.PATIENT,
                UserState.ENABLED
        );
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );

        Map<String, Object> pythonResponse = new LinkedHashMap<>();
        pythonResponse.put("symptoms", "headache");
        pythonResponse.put("disease", "migraine");
        when(recommendationService.recommendBySymptoms("headache")).thenReturn(pythonResponse);

        ResponseEntity<ApiRespDTO<Map<String, Object>>> response = controller.recommendBySymptoms("headache");
        assertEquals(200, response.getBody().getCode());
        assertEquals("migraine", response.getBody().getData().get("disease"));
    }
}
