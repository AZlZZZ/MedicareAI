package tn.esprit.medicareaitigerback.models.dto.response;

import  tn.esprit.medicareaitigerback.models.enumeration.GenderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    private GenderType gender;
}
