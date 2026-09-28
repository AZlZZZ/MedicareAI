package tn.esprit.medicareaitigerback.models.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.medicareaitigerback.models.enumeration.GenderType;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestPatientDTO {

    @NotBlank(message = "Username (email or phone) shouldn't be blank")
    private String username;

    @NotBlank(message = "Password shouldn't be blank")
    private String password;

    private String phonePrefix;

    // Optional profile fields collected at signup
    private String firstName;
    private String lastName;

    @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Invalid phone number")
    private String phoneNumber;

    private GenderType gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;
}
