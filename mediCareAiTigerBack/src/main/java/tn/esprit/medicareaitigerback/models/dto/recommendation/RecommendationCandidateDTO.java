package tn.esprit.medicareaitigerback.models.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationCandidateDTO {
    private Long medicamentId;
    private String name;
    private Integer descriptionLength;
    private Integer stock;
    private Integer scheduleCount;
    private Boolean isActive;
    private Integer totalAlerts;
    private Integer readAlerts;
    private Double adherenceRate;
}
