package tn.esprit.medicareaitigerback.models.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationRecommendationResponseDTO {
    private Long medicamentId;
    private String medicamentName;
    private Double score;
    private String reason;
    private String source;
}
