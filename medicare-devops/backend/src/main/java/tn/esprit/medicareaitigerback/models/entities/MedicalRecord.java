package tn.esprit.medicareaitigerback.models.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Audited
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "medical_record_seq")
    @SequenceGenerator(name = "medical_record_seq", sequenceName = "medical_record_sequence", allocationSize = 1)
    private Long id;

}
