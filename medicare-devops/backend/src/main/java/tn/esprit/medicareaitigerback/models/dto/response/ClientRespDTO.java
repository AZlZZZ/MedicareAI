package tn.esprit.medicareaitigerback.models.dto.response;

import  tn.esprit.medicareaitigerback.models.enumeration.GenderType;
import  tn.esprit.medicareaitigerback.models.enumeration.UserState;
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
public class ClientRespDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private GenderType gender;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Africa/Tunis")
    private Date createdAt;
    private UserState state;
    private String profilePhoto;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Africa/Tunis")
    private Date birthDate;

}
