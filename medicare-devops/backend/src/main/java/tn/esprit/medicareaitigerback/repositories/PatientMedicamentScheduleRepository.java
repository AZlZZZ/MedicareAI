package tn.esprit.medicareaitigerback.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.esprit.medicareaitigerback.models.entities.PatientMedicamentSchedule;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PatientMedicamentScheduleRepository extends JpaRepository<PatientMedicamentSchedule, Long> {
    List<PatientMedicamentSchedule> findByPatient_IdAndEndDateGreaterThanEqual(Long patientId, LocalDate date);

    @Query("SELECT s FROM PatientMedicamentSchedule s LEFT JOIN FETCH s.medicament LEFT JOIN FETCH s.patient WHERE s.startDate <= :toDate AND s.endDate >= :fromDate")
    List<PatientMedicamentSchedule> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(
            @org.springframework.data.repository.query.Param("toDate") LocalDate toDate,
            @org.springframework.data.repository.query.Param("fromDate") LocalDate fromDate);

    Optional<PatientMedicamentSchedule> findByIdAndPatient_Id(Long scheduleId, Long patientId);
}

