package tn.esprit.medicareaitigerback.services.interfaces;

import tn.esprit.medicareaitigerback.models.dto.response.PatientMedicamentAlertResponseDTO;

import java.util.List;

public interface IPatientMedicamentAlertService {

    List<PatientMedicamentAlertResponseDTO> getUpcomingAlerts(Long patientId, int weeks);

    void markAsRead(Long patientId, Long alertId);
}

