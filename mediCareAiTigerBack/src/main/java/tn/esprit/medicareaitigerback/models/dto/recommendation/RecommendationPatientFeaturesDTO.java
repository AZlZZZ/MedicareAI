package tn.esprit.medicareaitigerback.models.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationPatientFeaturesDTO {
    private Long patientId;
    private Integer age;
    private String gender;
    private Integer medicalHistoryLength;
    private Integer activeScheduleCount;
    private Integer totalAlerts;
    private Integer readAlerts;
    private Double readRate;
}
