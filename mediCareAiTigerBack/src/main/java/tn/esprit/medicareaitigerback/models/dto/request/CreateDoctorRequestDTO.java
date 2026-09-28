package tn.esprit.medicareaitigerback.models.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDoctorRequestDTO {
    @NotBlank(message = "firstName is required")
    private String firstName;
    @NotBlank(message = "lastName is required")
    private String lastName;
    private String phoneNumber;
    @NotBlank(message = "email is required")
    @Email(message = "Invalid email")
    private String email;
    @NotBlank(message = "password is required")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()\\-\\[{}\\]:;',?/*~$^+=<>]).{8,20}$",
        message = "Password must have 8-20 chars, 1 uppercase, 1 lowercase, 1 digit, 1 special char"
    )
    private String password;
    private Double homeLongitude;
    private Double homeLatitude;
    private String homeAddress;
    private String specialization;
}
