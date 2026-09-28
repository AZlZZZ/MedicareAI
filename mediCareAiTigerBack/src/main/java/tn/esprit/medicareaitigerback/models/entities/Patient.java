package tn.esprit.medicareaitigerback.models.entities;


import tn.esprit.medicareaitigerback.models.dto.request.PatientUpdateProfileDTO;
import tn.esprit.medicareaitigerback.models.dto.request.UpdateClientDTO;
import tn.esprit.medicareaitigerback.models.enumeration.ClientActivity;
import tn.esprit.medicareaitigerback.models.enumeration.GenderType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tn.esprit.medicareaitigerback.models.enumeration.UserState;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Patient extends User {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Africa/Tunis")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationPasswordDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Africa/Tunis")
    @Temporal(TemporalType.DATE)
    private Date birthDate;
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private ClientActivity activity = ClientActivity.OFFLINE;
    @Column(length = 50)
    private String phoneNumber;
    @Column(length = 4)
    private String phonePrefix;
    @Enumerated(EnumType.STRING)
    @Column(length = 2)
    private GenderType gender;
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    private Attachment profilePicture;

    private String medicalHistory;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Appointment> appointments = new ArrayList<>();
    public void fromDTO(PatientUpdateProfileDTO dto) {
        if (dto.getFirstName() != null) {
            this.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            this.setLastName(dto.getLastName());
        }
        if (dto.getEmail() != null) {
            this.setEmail(dto.getEmail());
        }
        if (dto.getBirthDate() != null) {
            try {
                this.birthDate = new SimpleDateFormat("dd/MM/yyyy").parse(dto.getBirthDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (dto.getGender() != null) {
            this.gender = dto.getGender();
        }
    }
    public void fromDTO(UpdateClientDTO dto) {
        this.setFirstName(dto.getFirstName());
        this.setLastName(dto.getLastName());
        this.setEmail(dto.getEmail());
        this.setState(UserState.ENABLED);
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isEmpty()) {
            this.phoneNumber = dto.getPhoneNumber();
        }
        if (dto.getBirthDate() != null) {
            try {
                this.birthDate = new SimpleDateFormat("dd/MM/yyyy").parse(dto.getBirthDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
