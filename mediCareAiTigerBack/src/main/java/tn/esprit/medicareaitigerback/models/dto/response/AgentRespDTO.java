package tn.esprit.medicareaitigerback.models.dto.response;

import tn.esprit.medicareaitigerback.models.enumeration.GenderType;
import tn.esprit.medicareaitigerback.models.enumeration.UserState;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentRespDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private UserState state;
    private RoleDTO role;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Africa/Tunis")
    private Date birthDate;
    private GenderType gender;
    private String phoneNumber;

}
