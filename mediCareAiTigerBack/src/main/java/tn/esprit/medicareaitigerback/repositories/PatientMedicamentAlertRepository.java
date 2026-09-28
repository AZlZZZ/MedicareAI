package tn.esprit.medicareaitigerback.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.medicareaitigerback.models.entities.PatientMedicamentAlert;
import tn.esprit.medicareaitigerback.models.enumeration.MedicationAlertStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PatientMedicamentAlertRepository extends JpaRepository<PatientMedicamentAlert, Long> {

    boolean existsBySchedule_IdAndTakeAt(Long scheduleId, LocalDateTime takeAt);

    List<PatientMedicamentAlert> findByPatient_IdAndTakeAtBetweenOrderByTakeAtAsc(Long patientId, LocalDateTime from, LocalDateTime to);

    List<PatientMedicamentAlert> findByEmailSentFalseAndAlertAtLessThanEqual(LocalDateTime now);

    Optional<PatientMedicamentAlert> findByIdAndPatient_Id(Long alertId, Long patientId);

    List<PatientMedicamentAlert> findByPatient_IdAndStatus(Long patientId, MedicationAlertStatus status);

    void deleteBySchedule_Id(Long scheduleId);

    @org.springframework.data.jpa.repository.Query("""
            SELECT a FROM PatientMedicamentAlert a
            LEFT JOIN FETCH a.schedule s
            LEFT JOIN FETCH s.medicament
            LEFT JOIN FETCH a.patient
            WHERE a.patient.id = :patientId
            ORDER BY a.takeAt DESC
            """)
    List<PatientMedicamentAlert> findDetailedByPatientId(
            @org.springframework.data.repository.query.Param("patientId") Long patientId
    );

    @org.springframework.data.jpa.repository.Query("""
            SELECT a FROM PatientMedicamentAlert a
            LEFT JOIN FETCH a.schedule s
            LEFT JOIN FETCH s.medicament
            LEFT JOIN FETCH a.patient
            ORDER BY a.id
            """)
    List<PatientMedicamentAlert> findAllDetailed();
}

