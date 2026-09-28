package tn.esprit.medicareaitigerback.models.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationEngineResponseDTO {
    private String modelVersion;
    private Boolean usedFallback;
    private List<RecommendationItemDTO> recommendations;
}
