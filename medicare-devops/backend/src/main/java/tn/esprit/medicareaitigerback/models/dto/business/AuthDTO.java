package tn.esprit.medicareaitigerback.models.dto.business;

import tn.esprit.medicareaitigerback.models.enumeration.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthDTO {
    private String username;
    private UserType userType;
}
