package tn.esprit.medicareaitigerback.models.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.medicareaitigerback.models.enumeration.WeekDay;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientMedicamentScheduleResponseDTO {
    private Long id;
    private Long patientId;
    private Long medicamentId;
    private String dose;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer alertBeforeMinutes;
    private List<WeekDay> daysOfWeek;
    private List<String> timesOfDay; // "HH:mm"
}

