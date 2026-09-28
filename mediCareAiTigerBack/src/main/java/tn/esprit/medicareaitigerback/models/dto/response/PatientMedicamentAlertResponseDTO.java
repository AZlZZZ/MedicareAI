package tn.esprit.medicareaitigerback.models.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.medicareaitigerback.models.enumeration.MedicationAlertStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientMedicamentAlertResponseDTO {
    private Long id;
    private Long medicamentId;
    private String medicamentName;
    private String dose;
    private LocalDateTime alertAt;
    private LocalDateTime takeAt;
    private MedicationAlertStatus status;
    private Boolean emailSent;
}

