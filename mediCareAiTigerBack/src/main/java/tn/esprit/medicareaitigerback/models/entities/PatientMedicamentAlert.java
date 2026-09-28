package tn.esprit.medicareaitigerback.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import tn.esprit.medicareaitigerback.models.enumeration.MedicationAlertStatus;

import java.time.LocalDateTime;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "patient_medicament_alert",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_patient_medicament_alert_schedule_takeAt", columnNames = {"schedule_id", "take_at"})
        }
)
public class PatientMedicamentAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "patient_medicament_alert_seq")
    @SequenceGenerator(
            name = "patient_medicament_alert_seq",
            sequenceName = "patient_medicament_alert_sequence",
            allocationSize = 1
    )
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schedule_id", nullable = false)
    private PatientMedicamentSchedule schedule;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "alert_at", nullable = false)
    private LocalDateTime alertAt;

    @Column(name = "take_at", nullable = false)
    private LocalDateTime takeAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private MedicationAlertStatus status = MedicationAlertStatus.UNREAD;

    @Column(name = "email_sent", nullable = false)
    private Boolean emailSent = false;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

