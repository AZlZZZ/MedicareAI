package tn.esprit.medicareaitigerback.models.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateClientDTO {
    @NotBlank(message = "firstName shouldn't be blank")
    private String firstName;
    @NotBlank(message = "lastName shouldn't be blank")
    private String lastName;
    @Email(message = "Email address not valid")
    private String email;
    private String phoneNumber;
    @Pattern(
            regexp = "^\\d{2}/\\d{2}/\\d{4}$",
            message = "Date must be formatted dd/MM/yyyy"
    )
    private String birthDate;

}
