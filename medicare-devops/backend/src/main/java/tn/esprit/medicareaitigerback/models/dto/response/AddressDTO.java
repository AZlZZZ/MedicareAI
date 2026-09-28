package tn.esprit.medicareaitigerback.models.dto.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AddressDTO {
    private Long id;
    private String address;
    private Double longitude;
    private Double latitude;
    @Enumerated(EnumType.STRING)
    private String libelle;
    private Boolean isDefault;
}
