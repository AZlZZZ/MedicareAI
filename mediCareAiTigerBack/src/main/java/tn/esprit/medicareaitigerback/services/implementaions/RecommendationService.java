package tn.esprit.medicareaitigerback.services.implementaions;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import tn.esprit.medicareaitigerback.models.dto.recommendation.RecommendationCandidateDTO;
import tn.esprit.medicareaitigerback.models.dto.recommendation.RecommendationEngineRequestDTO;
import tn.esprit.medicareaitigerback.models.dto.recommendation.RecommendationEngineResponseDTO;
import tn.esprit.medicareaitigerback.models.dto.recommendation.RecommendationItemDTO;
import tn.esprit.medicareaitigerback.models.dto.recommendation.RecommendationPatientFeaturesDTO;
import tn.esprit.medicareaitigerback.models.dto.response.MedicationRecommendationResponseDTO;
import tn.esprit.medicareaitigerback.models.entities.Medicament;
import tn.esprit.medicareaitigerback.models.entities.Patient;
import tn.esprit.medicareaitigerback.models.entities.PatientMedicamentAlert;
import tn.esprit.medicareaitigerback.models.entities.PatientMedicamentSchedule;
import tn.esprit.medicareaitigerback.models.enumeration.MedicationAlertStatus;
import tn.esprit.medicareaitigerback.repositories.MedicamentRepository;
import tn.esprit.medicareaitigerback.repositories.PatientMedicamentAlertRepository;
import tn.esprit.medicareaitigerback.repositories.PatientMedicamentScheduleRepository;
import tn.esprit.medicareaitigerback.repositories.PatientRepository;
import tn.esprit.medicareaitigerback.services.interfaces.IRecommendationService;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class RecommendationService implements IRecommendationService {

    private final RestTemplate restTemplate;
    private final PatientRepository patientRepository;
    private final MedicamentRepository medicamentRepository;
    private final PatientMedicamentScheduleRepository scheduleRepository;
    private final PatientMedicamentAlertRepository alertRepository;

    @Value("${ai.recommendation.base-url:http://localhost:8000}")
    private String recommendationBaseUrl;

    @Override
    @Transactional(readOnly = true)
    public List<MedicationRecommendationResponseDTO> recommendForPatient(Long patientId, int topK) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalStateException("Patient not found: " + patientId));
        List<PatientMedicamentSchedule> schedules = scheduleRepository.findDetailedByPatientId(patientId);
        List<PatientMedicamentAlert> alerts = alertRepository.findDetailedByPatientId(patientId);
        List<Medicament> medicaments = medicamentRepository.findAll();

        RecommendationPatientFeaturesDTO patientFeatures = buildPatientFeatures(patient, schedules, alerts);
        List<RecommendationCandidateDTO> candidates = buildCandidates(medicaments, schedules, alerts);
        RecommendationEngineRequestDTO request = new RecommendationEngineRequestDTO(patientFeatures, candidates, Math.max(1, topK));

        try {
            RecommendationEngineResponseDTO response = restTemplate.postForObject(
                    recommendationBaseUrl + "/recommend",
                    request,
                    RecommendationEngineResponseDTO.class
            );
            if (response == null || response.getRecommendations() == null || response.getRecommendations().isEmpty()) {
                return fallback(candidates, medicaments, topK);
            }
            Map<Long, String> medicamentNames = medicaments.stream()
                    .collect(Collectors.toMap(Medicament::getId, Medicament::getName, (a, b) -> a));
            String source = Boolean.TRUE.equals(response.getUsedFallback()) ? "python-fallback" : "python-model";
            return response.getRecommendations().stream()
                    .limit(topK)
                    .map(item -> new MedicationRecommendationResponseDTO(
                            item.getMedicamentId(),
                            medicamentNames.getOrDefault(item.getMedicamentId(), "unknown"),
                            item.getScore(),
                            item.getReason(),
                            source
                    ))
                    .collect(Collectors.toList());
        } catch (RestClientException ex) {
            return fallback(candidates, medicaments, topK);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> recommendBySymptoms(String symptoms) {
        String normalizedSymptoms = symptoms == null ? "" : symptoms.trim();
        if (normalizedSymptoms.isEmpty()) {
            throw new IllegalStateException("symptoms is required");
        }
        String url = UriComponentsBuilder
                .fromHttpUrl(recommendationBaseUrl)
                .path("/recommend")
                .queryParam("symptoms", normalizedSymptoms)
                .toUriString();
        try {
            Map response = restTemplate.getForObject(url, Map.class);
            if (response == null) {
                Map<String, Object> fallback = new LinkedHashMap<>();
                fallback.put("source", "spring-fallback");
                fallback.put("symptoms", normalizedSymptoms);
                fallback.put("message", "Empty response from python API");
                return fallback;
            }
            return response;
        } catch (RestClientException ex) {
            Map<String, Object> fallback = new LinkedHashMap<>();
            fallback.put("source", "spring-fallback");
            fallback.put("symptoms", normalizedSymptoms);
            fallback.put("message", "Python recommendation API unavailable");
            return fallback;
        }
    }

    private RecommendationPatientFeaturesDTO buildPatientFeatures(
            Patient patient,
            List<PatientMedicamentSchedule> schedules,
            List<PatientMedicamentAlert> alerts
    ) {
        int age = -1;
        if (patient.getBirthDate() != null) {
            LocalDate birthDate = new java.sql.Date(patient.getBirthDate().getTime()).toLocalDate();
            age = Period.between(birthDate, LocalDate.now()).getYears();
        }
        int readAlerts = (int) alerts.stream().filter(a -> a.getStatus() == MedicationAlertStatus.READ).count();
        int totalAlerts = alerts.size();
        double readRate = totalAlerts == 0 ? 0.0 : (double) readAlerts / totalAlerts;
        int activeSchedules = (int) schedules.stream().filter(s -> !s.getEndDate().isBefore(LocalDate.now())).count();

        return new RecommendationPatientFeaturesDTO(
                patient.getId(),
                age,
                patient.getGender() == null ? "UNKNOWN" : patient.getGender().name(),
                patient.getMedicalHistory() == null ? 0 : patient.getMedicalHistory().length(),
                activeSchedules,
                totalAlerts,
                readAlerts,
                readRate
        );
    }

    private List<RecommendationCandidateDTO> buildCandidates(
            List<Medicament> medicaments,
            List<PatientMedicamentSchedule> schedules,
            List<PatientMedicamentAlert> alerts
    ) {
        Map<Long, Integer> scheduleCountByMedication = new HashMap<>();
        Map<Long, Integer> totalAlertsByMedication = new HashMap<>();
        Map<Long, Integer> readAlertsByMedication = new HashMap<>();
        Map<Long, Boolean> activeByMedication = new HashMap<>();

        for (PatientMedicamentSchedule schedule : schedules) {
            Long medicamentId = schedule.getMedicament().getId();
            scheduleCountByMedication.merge(medicamentId, 1, Integer::sum);
            boolean isActive = !schedule.getEndDate().isBefore(LocalDate.now());
            activeByMedication.put(medicamentId, activeByMedication.getOrDefault(medicamentId, false) || isActive);
        }
        for (PatientMedicamentAlert alert : alerts) {
            Long medicamentId = alert.getSchedule().getMedicament().getId();
            totalAlertsByMedication.merge(medicamentId, 1, Integer::sum);
            if (alert.getStatus() == MedicationAlertStatus.READ) {
                readAlertsByMedication.merge(medicamentId, 1, Integer::sum);
            }
        }

        List<RecommendationCandidateDTO> result = new ArrayList<>();
        for (Medicament medicament : medicaments) {
            int totalAlerts = totalAlertsByMedication.getOrDefault(medicament.getId(), 0);
            int readAlerts = readAlertsByMedication.getOrDefault(medicament.getId(), 0);
            double adherenceRate = totalAlerts == 0 ? 0.0 : (double) readAlerts / totalAlerts;
            result.add(new RecommendationCandidateDTO(
                    medicament.getId(),
                    medicament.getName(),
                    medicament.getDescription() == null ? 0 : medicament.getDescription().length(),
                    medicament.getStock() == null ? 0 : medicament.getStock(),
                    scheduleCountByMedication.getOrDefault(medicament.getId(), 0),
                    activeByMedication.getOrDefault(medicament.getId(), false),
                    totalAlerts,
                    readAlerts,
                    adherenceRate
            ));
        }
        return result;
    }

    private List<MedicationRecommendationResponseDTO> fallback(List<RecommendationCandidateDTO> candidates, List<Medicament> medicaments, int topK) {
        Map<Long, String> names = medicaments.stream()
                .collect(Collectors.toMap(Medicament::getId, Medicament::getName, (a, b) -> a));

        return candidates.stream()
                .sorted(Comparator
                        .comparing(RecommendationCandidateDTO::getIsActive)
                        .thenComparing(RecommendationCandidateDTO::getAdherenceRate)
                        .thenComparing(RecommendationCandidateDTO::getStock)
                        .reversed())
                .limit(topK)
                .map(c -> new MedicationRecommendationResponseDTO(
                        c.getMedicamentId(),
                        names.getOrDefault(c.getMedicamentId(), "unknown"),
                        0.5 + (Boolean.TRUE.equals(c.getIsActive()) ? 0.2 : 0.0) + (0.2 * c.getAdherenceRate()),
                        "local_fallback_rule",
                        "spring-fallback"
                ))
                .collect(Collectors.toList());
    }
}
