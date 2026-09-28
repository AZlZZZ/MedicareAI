package tn.esprit.medicareaitigerback.models.dto.request;

import tn.esprit.medicareaitigerback.models.enumeration.GenderType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterAgentDTO extends RegisterUserDTO {
    @NotNull(message = "role shouldn't be null")
    private Long roleId;
//    @NotNull(message = "golden dealer  shouldn't be null")
//    private Long goldenId;
    @Pattern(
            regexp = "^\\d{2}/\\d{2}/\\d{4}$",
            message = "Date must be formatted dd/MM/yyyy"
    )
    private String birthDate;
//    private List<Long> regionDTOs;
    private GenderType gender;
    @Pattern(regexp = "(^$|\\d{8})", message = "Phone Number must have 8 digits")
    private String phoneNumber;



}
