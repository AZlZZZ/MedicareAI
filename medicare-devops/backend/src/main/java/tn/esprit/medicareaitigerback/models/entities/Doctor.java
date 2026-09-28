package tn.esprit.medicareaitigerback.models.entities;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tn.esprit.medicareaitigerback.models.enumeration.ClientActivity;
import tn.esprit.medicareaitigerback.models.enumeration.GenderType;

import java.util.Date;

@Entity
@Audited
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Doctor extends User {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Africa/Tunis")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationPasswordDate;
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
    private String specialization;
    private boolean isAvailable;

}
