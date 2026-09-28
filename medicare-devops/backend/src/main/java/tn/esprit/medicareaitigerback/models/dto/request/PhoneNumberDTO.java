package tn.esprit.medicareaitigerback.models.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhoneNumberDTO {
    @NotBlank(message = "Phone number shouldn't be blank")
    private String phoneNumber;
    @NotBlank(message = "Phone number shouldn't be blank")
    private String phonePrefix;
    private String deviceId;
    private String deliveryMethod; //"SMS" or "WHATSAPP"
}
