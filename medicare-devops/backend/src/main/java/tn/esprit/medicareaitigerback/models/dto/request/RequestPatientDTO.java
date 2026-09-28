package tn.esprit.medicareaitigerback.models.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestPatientDTO {
    @NotBlank(message = "Username (email or phone) shouldn't be blank")
    private String username;

    @NotBlank(message = "Password shouldn't be blank")
    private String password;
    private String phonePrefix;

}
