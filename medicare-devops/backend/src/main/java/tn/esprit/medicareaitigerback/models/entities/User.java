package tn.esprit.medicareaitigerback.models.entities;

import tn.esprit.medicareaitigerback.models.enumeration.UserState;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(name = "user_entity")
@Audited
@EntityListeners(AuditingEntityListener.class)
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 1)
    private Long id;
    @Column(length = 50)
    private String firstName;
    @Column(length = 50)
    private String lastName;
    @Column(length = 50)
    private String userName;
    @Column(length = 50)
    private String email;
    private String deleteCause;
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private UserState state = UserState.NON_ACTIV;

    private Boolean isDeletable = true;
    private Boolean isEmailValidated = false;
    private Boolean isPhoneNumberValidated = true;
    private LocalDate unblockDate;
    private String refreshToken;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @NotAudited
    private List<Address> addresses = new ArrayList<>();



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
