package tn.esprit.medicareaitigerback.models.dto.response;

import  tn.esprit.medicareaitigerback.models.enumeration.ReasonType;
import  tn.esprit.medicareaitigerback.models.enumeration.UserType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReasonResponseDTO {
    private Long id;
    private String reason;
    @Enumerated(EnumType.STRING)
    private UserType userType;
    @Enumerated(EnumType.STRING)
    private ReasonType reasonType;
}
