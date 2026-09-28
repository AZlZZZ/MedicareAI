package tn.esprit.medicareaitigerback.services.interfaces;

import tn.esprit.medicareaitigerback.models.dto.request.CreatePatientMedicamentScheduleDTO;
import tn.esprit.medicareaitigerback.models.dto.response.PatientMedicamentScheduleResponseDTO;

public interface IPatientMedicamentScheduleService {

    PatientMedicamentScheduleResponseDTO create(Long patientId, CreatePatientMedicamentScheduleDTO dto);

    PatientMedicamentScheduleResponseDTO edit(Long scheduleId, CreatePatientMedicamentScheduleDTO dto);

    void delete(Long scheduleId);

    java.util.List<PatientMedicamentScheduleResponseDTO> findByPatient(Long patientId);
}

