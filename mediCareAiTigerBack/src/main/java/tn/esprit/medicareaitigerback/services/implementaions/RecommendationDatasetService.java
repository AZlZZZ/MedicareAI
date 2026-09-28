package tn.esprit.medicareaitigerback.services.implementaions;

import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.medicareaitigerback.models.entities.Medicament;
import tn.esprit.medicareaitigerback.models.entities.Patient;
import tn.esprit.medicareaitigerback.models.entities.PatientMedicamentAlert;
import tn.esprit.medicareaitigerback.models.entities.PatientMedicamentSchedule;
import tn.esprit.medicareaitigerback.models.enumeration.MedicationAlertStatus;
import tn.esprit.medicareaitigerback.repositories.MedicamentRepository;
import tn.esprit.medicareaitigerback.repositories.PatientMedicamentAlertRepository;
import tn.esprit.medicareaitigerback.repositories.PatientMedicamentScheduleRepository;
import tn.esprit.medicareaitigerback.repositories.PatientRepository;
import tn.esprit.medicareaitigerback.services.interfaces.IRecommendationDatasetService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RecommendationDatasetService implements IRecommendationDatasetService {

    private final PatientRepository patientRepository;
    private final MedicamentRepository medicamentRepository;
    private final PatientMedicamentScheduleRepository scheduleRepository;
    private final PatientMedicamentAlertRepository alertRepository;

    @Override
    @Transactional(readOnly = true)
    public Resource exportPatientFeatures() throws IOException {
        List<Patient> patients = patientRepository.findAllByOrderByIdAsc();
        List<PatientMedicamentSchedule> schedules = scheduleRepository.findAllDetailed();
        List<PatientMedicamentAlert> alerts = alertRepository.findAllDetailed();

        Map<Long, Long> activeSchedulesByPatient = schedules.stream()
                .filter(s -> !s.getEndDate().isBefore(LocalDate.now()))
                .collect(Collectors.groupingBy(s -> s.getPatient().getId(), Collectors.counting()));
        Map<Long, Long> totalAlertsByPatient = alerts.stream()
                .collect(Collectors.groupingBy(a -> a.getPatient().getId(), Collectors.counting()));
        Map<Long, Long> readAlertsByPatient = alerts.stream()
                .filter(a -> a.getStatus() == MedicationAlertStatus.READ)
                .collect(Collectors.groupingBy(a -> a.getPatient().getId(), Collectors.counting()));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        writer.write("patient_id;age;gender;medical_history_length;active_schedule_count;total_alerts;read_alerts;read_rate\n");

        for (Patient patient : patients) {
            long totalAlerts = totalAlertsByPatient.getOrDefault(patient.getId(), 0L);
            long readAlerts = readAlertsByPatient.getOrDefault(patient.getId(), 0L);
            double readRate = totalAlerts == 0 ? 0.0 : (double) readAlerts / totalAlerts;
            writer.write(String.format(
                    "%d;%d;%s;%d;%d;%d;%d;%.4f%n",
                    patient.getId(),
                    calculateAge(patient),
                    sanitize(patient.getGender() == null ? "UNKNOWN" : patient.getGender().name()),
                    safeLength(patient.getMedicalHistory()),
                    activeSchedulesByPatient.getOrDefault(patient.getId(), 0L),
                    totalAlerts,
                    readAlerts,
                    readRate
            ));
        }
        writer.flush();
        writer.close();
        return new ByteArrayResource(outputStream.toByteArray());
    }

    @Override
    @Transactional(readOnly = true)
    public Resource exportMedicationFeatures() throws IOException {
        List<Medicament> medicaments = medicamentRepository.findAll();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        writer.write("medicament_id;name;description_length;stock\n");

        for (Medicament medicament : medicaments) {
            writer.write(String.format(
                    "%d;%s;%d;%d%n",
                    medicament.getId(),
                    sanitize(medicament.getName()),
                    safeLength(medicament.getDescription()),
                    medicament.getStock() == null ? 0 : medicament.getStock()
            ));
        }
        writer.flush();
        writer.close();
        return new ByteArrayResource(outputStream.toByteArray());
    }

    @Override
    @Transactional(readOnly = true)
    public Resource exportPatientMedicationInteractions() throws IOException {
        List<PatientMedicamentSchedule> schedules = scheduleRepository.findAllDetailed();
        List<PatientMedicamentAlert> alerts = alertRepository.findAllDetailed();

        Map<String, Long> totalAlerts = alerts.stream()
                .collect(Collectors.groupingBy(a -> key(a.getPatient().getId(), a.getSchedule().getMedicament().getId()), Collectors.counting()));
        Map<String, Long> readAlerts = alerts.stream()
                .filter(a -> a.getStatus() == MedicationAlertStatus.READ)
                .collect(Collectors.groupingBy(a -> key(a.getPatient().getId(), a.getSchedule().getMedicament().getId()), Collectors.counting()));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        writer.write("patient_id;medicament_id;schedule_count;is_active;total_alerts;read_alerts;adherence_rate;label\n");

        Map<String, List<PatientMedicamentSchedule>> grouped = schedules.stream()
                .collect(Collectors.groupingBy(s -> key(s.getPatient().getId(), s.getMedicament().getId())));

        for (Map.Entry<String, List<PatientMedicamentSchedule>> entry : grouped.entrySet()) {
            String pairKey = entry.getKey();
            List<PatientMedicamentSchedule> pairSchedules = entry.getValue();
            PatientMedicamentSchedule sample = pairSchedules.get(0);
            long scheduleCount = pairSchedules.size();
            boolean hasActive = pairSchedules.stream().anyMatch(s -> !s.getEndDate().isBefore(LocalDate.now()));
            long pairTotalAlerts = totalAlerts.getOrDefault(pairKey, 0L);
            long pairReadAlerts = readAlerts.getOrDefault(pairKey, 0L);
            double adherenceRate = pairTotalAlerts == 0 ? 0.0 : (double) pairReadAlerts / pairTotalAlerts;
            int label = hasActive || adherenceRate >= 0.5 ? 1 : 0;

            writer.write(String.format(
                    "%d;%d;%d;%s;%d;%d;%.4f;%d%n",
                    sample.getPatient().getId(),
                    sample.getMedicament().getId(),
                    scheduleCount,
                    hasActive,
                    pairTotalAlerts,
                    pairReadAlerts,
                    adherenceRate,
                    label
            ));
        }

        writer.flush();
        writer.close();
        return new ByteArrayResource(outputStream.toByteArray());
    }

    private int calculateAge(Patient patient) {
        if (patient.getBirthDate() == null) {
            return -1;
        }
        LocalDate birthDate = new java.sql.Date(patient.getBirthDate().getTime()).toLocalDate();
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    private int safeLength(String value) {
        return value == null ? 0 : value.length();
    }

    private String sanitize(String value) {
        if (value == null) {
            return "";
        }
        return value.replace(';', ',').replace('\n', ' ').replace('\r', ' ').trim();
    }

    private String key(Long patientId, Long medicamentId) {
        return patientId + ":" + medicamentId;
    }
}
