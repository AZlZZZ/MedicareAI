package tn.esprit.medicareaitigerback.models.dto.recommendation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationEngineRequestDTO {
    private RecommendationPatientFeaturesDTO patient;
    private List<RecommendationCandidateDTO> candidates;
    @JsonProperty("top_k")
    private Integer topK;
}
