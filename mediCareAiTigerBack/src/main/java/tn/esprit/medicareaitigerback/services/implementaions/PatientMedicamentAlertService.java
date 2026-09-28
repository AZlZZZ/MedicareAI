package tn.esprit.medicareaitigerback.services.implementaions;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.medicareaitigerback.models.dto.response.PatientMedicamentAlertResponseDTO;
import tn.esprit.medicareaitigerback.models.entities.PatientMedicamentAlert;
import tn.esprit.medicareaitigerback.repositories.PatientMedicamentAlertRepository;
import tn.esprit.medicareaitigerback.services.interfaces.IPatientMedicamentAlertService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static tn.esprit.medicareaitigerback.models.enumeration.MedicationAlertStatus.READ;

@Service
@AllArgsConstructor
public class PatientMedicamentAlertService implements IPatientMedicamentAlertService {

    private final PatientMedicamentAlertRepository alertRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PatientMedicamentAlertResponseDTO> getUpcomingAlerts(Long patientId, int weeks) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime to = now.plusWeeks(Math.max(0, weeks));

        List<PatientMedicamentAlert> alerts =
                alertRepository.findByPatient_IdAndTakeAtBetweenOrderByTakeAtAsc(patientId, now, to);

        return alerts.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long patientId, Long alertId) {
        PatientMedicamentAlert alert = alertRepository
                .findByIdAndPatient_Id(alertId, patientId)
                .orElseThrow(() -> new IllegalStateException("Alert not found: " + alertId));

        alert.setStatus(READ);
        alert.setReadAt(LocalDateTime.now());
        alertRepository.save(alert);
    }

    private PatientMedicamentAlertResponseDTO toDTO(PatientMedicamentAlert alert) {
        var schedule = alert.getSchedule();
        var medicament = schedule.getMedicament();
        return new PatientMedicamentAlertResponseDTO(
                alert.getId(),
                medicament.getId(),
                medicament.getName(),
                schedule.getDose(),
                alert.getAlertAt(),
                alert.getTakeAt(),
                alert.getStatus(),
                alert.getEmailSent()
        );
    }
}

