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
public class CreatePatientRequestDTO {
    @NotBlank(message = "firstName is required")
    private String firstName;
    @NotBlank(message = "lastName is required")
    private String lastName;
    @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Invalid phone number")
    private String phoneNumber;
    @NotBlank(message = "email is required")
    @Email(message = "Invalid email address")
    private String email;
    private Double homeLongitude;
    private Double homeLatitude;
    private String homeAddress;
}
