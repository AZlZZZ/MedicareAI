package tn.esprit.medicareaitigerback.models.dto.request;

import tn.esprit.medicareaitigerback.models.enumeration.AddressType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressAddDTO {
    @NotBlank(message = "address shouldn't be blank")
    private String address;
    @NotNull(message = "longitude shouldn't be null")
    private Double longitude;
    @NotNull(message = "latitude shouldn't be null")
    private Double latitude;
    @Enumerated(EnumType.STRING)
    private AddressType addressType;
    private String libelle;
}
