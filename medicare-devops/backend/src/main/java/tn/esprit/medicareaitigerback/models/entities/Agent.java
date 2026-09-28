package tn.esprit.medicareaitigerback.models.entities;

import tn.esprit.medicareaitigerback.models.dto.request.RegisterAgentDTO;
import tn.esprit.medicareaitigerback.models.enumeration.GenderType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Entity
@Audited
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class Agent extends User {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Africa/Tunis")
    @Temporal(TemporalType.TIMESTAMP)
    private Date birthDate;
    @Enumerated(EnumType.STRING)
    @Column(length = 2)
    private GenderType gender;
    private String phoneNumber;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Role role;



}
