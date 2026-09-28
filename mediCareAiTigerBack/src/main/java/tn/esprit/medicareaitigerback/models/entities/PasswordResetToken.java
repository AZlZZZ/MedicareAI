package tn.esprit.medicareaitigerback.models.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "passwordResetToken_seq")
    @SequenceGenerator(name = "passwordResetToken_seq", sequenceName = "passwordResetToken_sequence", allocationSize = 1)
    private Long id;
    private String token;

    @OneToOne
    @CreatedBy
    private User user;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Africa/Tunis")
    private Date expirationDate;
    @CreatedDate
    private Date createdAt;


    public PasswordResetToken(String token, User user) {
        this.token = token;
        this.user = user;
        this.expirationDate = new Date(System.currentTimeMillis() + 60 * 60 * 1000);
    }
}

