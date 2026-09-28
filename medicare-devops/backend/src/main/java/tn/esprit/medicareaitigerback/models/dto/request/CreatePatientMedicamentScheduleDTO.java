package tn.esprit.medicareaitigerback.models.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.medicareaitigerback.models.enumeration.WeekDay;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePatientMedicamentScheduleDTO {

    @NotNull
    private Long medicamentId;

    @NotBlank
    @Size(max = 200)
    private String dose;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate; // optional; if null, default is applied in service

    @NotNull
    private Integer alertBeforeMinutes;

    @NotNull
    @Size(min = 1)
    private List<WeekDay> daysOfWeek;

    @NotNull
    @Size(min = 1)
    private List<String> timesOfDay; // "HH:mm"
}

