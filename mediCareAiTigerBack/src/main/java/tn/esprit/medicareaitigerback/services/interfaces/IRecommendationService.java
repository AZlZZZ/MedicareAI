package tn.esprit.medicareaitigerback.services.interfaces;

import tn.esprit.medicareaitigerback.models.dto.response.MedicationRecommendationResponseDTO;

import java.util.List;
import java.util.Map;

public interface IRecommendationService {
    List<MedicationRecommendationResponseDTO> recommendForPatient(Long patientId, int topK);
    Map<String, Object> recommendBySymptoms(String symptoms);
}
