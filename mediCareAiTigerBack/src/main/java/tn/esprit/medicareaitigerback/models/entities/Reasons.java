package tn.esprit.medicareaitigerback.models.entities;

import tn.esprit.medicareaitigerback.models.enumeration.Language;
import tn.esprit.medicareaitigerback.models.enumeration.ReasonType;
import tn.esprit.medicareaitigerback.models.enumeration.UserType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Entity
@Audited
@EntityListeners(AuditingEntityListener.class)
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reasons {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reasons_seq")
    @SequenceGenerator(name = "reasons_seq", sequenceName = "reasons_sequence", allocationSize = 1)
    private Long id;
    private String reason;
    @Enumerated(EnumType.STRING)
    @Column(length = 25)
    private UserType userType;
    @Enumerated(EnumType.STRING)
    @Column(length = 25)
    private ReasonType reasonType;
    @Enumerated(EnumType.STRING)
    @Column(length = 2)
    private Language language;
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @CreatedBy
    private User createdBy;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @LastModifiedBy
    private User updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Africa/Tunis")
    @CreatedDate
    private Date createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Africa/Tunis")
    @LastModifiedDate
    private Date modifiedAt;

    }
