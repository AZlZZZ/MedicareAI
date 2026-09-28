package tn.esprit.medicareaitigerback.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import tn.esprit.medicareaitigerback.models.enumeration.WeekDay;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientMedicamentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "patient_medicament_schedule_seq")
    @SequenceGenerator(
            name = "patient_medicament_schedule_seq",
            sequenceName = "patient_medicament_schedule_sequence",
            allocationSize = 1
    )
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medicament_id", nullable = false)
    private Medicament medicament;

    @Column(nullable = false, length = 200)
    private String dose;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Integer alertBeforeMinutes;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "patient_medicament_schedule_days",
            joinColumns = @JoinColumn(name = "schedule_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "day", nullable = false, length = 15)
    private List<WeekDay> daysOfWeek = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "patient_medicament_schedule_times",
            joinColumns = @JoinColumn(name = "schedule_id")
    )
    @Column(name = "time", nullable = false)
    private List<LocalTime> timesOfDay = new ArrayList<>();
}

