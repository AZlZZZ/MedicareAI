package tn.esprit.medicareaitigerback.models.dto.request;

import lombok.NoArgsConstructor;
import tn.esprit.medicareaitigerback.models.enumeration.GenderType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientUpdateProfileDTO {
    private String firstName;
    private String lastName;
    @Email(message = "Email Adress not valid")
    private String email;
    @Pattern(
            regexp = "^\\d{2}/\\d{2}/\\d{4}$",
            message = "Date must be formatted dd/MM/yyyy"
    )
    private String birthDate;
    private GenderType gender;

}
