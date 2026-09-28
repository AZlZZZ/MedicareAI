package tn.esprit.medicareaitigerback.models.dto.response;

import  tn.esprit.medicareaitigerback.models.enumeration.UserState;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentDetails {
    private String firstName;
    private String lastName;
    private String roleName;
    private UserState state;
}
