package tn.esprit.medicareaitigerback.models.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationItemDTO {
    private Long medicamentId;
    private Double score;
    private String reason;
}
