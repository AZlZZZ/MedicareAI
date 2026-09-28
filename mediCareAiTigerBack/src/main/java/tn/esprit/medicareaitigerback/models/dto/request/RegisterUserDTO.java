package tn.esprit.medicareaitigerback.models.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserDTO {
    @NotBlank(message = "firstName shouldn't be blank")
    private String firstName;
    @NotBlank(message = "lastName shouldn't be blank")
    private String lastName;
    @NotBlank(message = "Email shouldn't be blank")
    @Email(message = "Email Adress not valid")
    private String email;
    @NotBlank(message = "password shouldn't be blank")
    @Pattern(regexp = ("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$"),
            message = "Password must contains at least one UPERCASE, one LOWERCASE, one DIGIT, one SPECIAL CHARCTER and size between 8 et 20")
    private String password;
}
