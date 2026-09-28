package tn.esprit.medicareaitigerback.models.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.medicareaitigerback.models.enumeration.GenderType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorRegisterDTO {
    @NotBlank(message = "firstName shouldn't be blank")
    private String firstName;
    @NotBlank(message = "lastName shouldn't be blank")
    private String lastName;
    @Email(message = "Email Address not valid")
    private String email;
    @Pattern(
            regexp = "^\\d{2}/\\d{2}/\\d{4}$",
            message = "Date must be formatted dd/MM/yyyy"
    )
    private String birthDate;
    private GenderType gender;
    private String specialization;



}
