package tn.esprit.medicareaitigerback.models.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

public class Diagnostic {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "diagnostic_seq")
    @SequenceGenerator(name = "diagnostic_seq", sequenceName = "diagnostic_sequence", allocationSize = 1)
    private Long id;

    private String diseaseName; // e.g., "Diabetes", "Hypertension"
    private String severity;    // e.g., "Chronic", "Acute"
    private LocalDate diagnosisDate;

    @ManyToOne
    @JoinColumn(name = "record_id")
    private MedicalRecord medicalRecord;
}
