package tn.esprit.medicareaitigerback.schedulers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Component;
import tn.esprit.medicareaitigerback.models.entities.PatientMedicamentAlert;
import tn.esprit.medicareaitigerback.models.entities.PatientMedicamentSchedule;
import tn.esprit.medicareaitigerback.models.enumeration.MedicationAlertStatus;
import tn.esprit.medicareaitigerback.models.enumeration.WeekDay;
import tn.esprit.medicareaitigerback.repositories.PatientMedicamentAlertRepository;
import tn.esprit.medicareaitigerback.repositories.PatientMedicamentScheduleRepository;
import tn.esprit.medicareaitigerback.services.interfaces.IMedicamentReminderEmailService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class MedicamentAlertScheduler {

    private final PatientMedicamentScheduleRepository scheduleRepository;
    private final PatientMedicamentAlertRepository alertRepository;
    private final IMedicamentReminderEmailService emailService;

    private static final int LOOKBACK_DAYS = 1;
    private static final int LOOKAHEAD_DAYS = 45;

    @Scheduled(fixedDelayString = "${medicament.alerts.jobDelayMs:60000}")
    @Transactional
    public void generateAlertsAndSendEmails() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate fromDate = now.toLocalDate().minusDays(LOOKBACK_DAYS);
        LocalDate toDate = now.toLocalDate().plusDays(LOOKAHEAD_DAYS);

        // 1) Generate missing alerts for active schedules
        List<PatientMedicamentSchedule> schedules =
                scheduleRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(toDate, fromDate);

        log.info("Scheduler — found {} active schedules (fromDate={} toDate={})", schedules.size(), fromDate, toDate);

        for (PatientMedicamentSchedule schedule : schedules) {
            generateAlertsForSchedule(schedule, fromDate, toDate, now);
        }

        // 2) Send email reminders when alertAt is reached and email not sent yet
        List<PatientMedicamentAlert> dueAlerts = alertRepository.findByEmailSentFalseAndAlertAtLessThanEqual(now);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (PatientMedicamentAlert alert : dueAlerts) {
            try {
                if (alert.getPatient() == null || alert.getPatient().getEmail() == null || alert.getPatient().getEmail().isBlank()) {
                    alert.setEmailSent(true); // avoid re-trying forever
                    alertRepository.save(alert);
                    continue;
                }

                var medicament = alert.getSchedule().getMedicament();
                String formattedTakeAt = alert.getTakeAt().format(formatter);

                emailService.sendMedicamentReminder(
                        alert.getPatient().getEmail(),
                        medicament.getName(),
                        alert.getSchedule().getDose(),
                        formattedTakeAt
                );

                alert.setEmailSent(true);
                alertRepository.save(alert);
            } catch (Exception e) {
                log.warn("Failed to send medicament reminder email for alertId={}: {}", alert.getId(), e.getMessage());
            }
        }
    }

    private void generateAlertsForSchedule(
            PatientMedicamentSchedule schedule,
            LocalDate fromDate,
            LocalDate toDate,
            LocalDateTime createdAt
    ) {
        LocalDate scheduleFrom = schedule.getStartDate().isAfter(fromDate) ? schedule.getStartDate() : fromDate;
        LocalDate scheduleTo = schedule.getEndDate().isBefore(toDate) ? schedule.getEndDate() : toDate;
        if (scheduleFrom.isAfter(scheduleTo)) {
            return;
        }

        for (LocalDate date = scheduleFrom; !date.isAfter(scheduleTo); date = date.plusDays(1)) {
            WeekDay day = WeekDay.fromJavaDayOfWeek(date.getDayOfWeek());
            if (!schedule.getDaysOfWeek().contains(day)) {
                continue;
            }

            for (var time : schedule.getTimesOfDay()) {
                LocalDateTime takeAt = LocalDateTime.of(date, time);
                if (alertRepository.existsBySchedule_IdAndTakeAt(schedule.getId(), takeAt)) {
                    continue;
                }

                LocalDateTime alertAt = takeAt.minusMinutes(schedule.getAlertBeforeMinutes());

                PatientMedicamentAlert alert = new PatientMedicamentAlert();
                alert.setSchedule(schedule);
                alert.setPatient(schedule.getPatient());
                alert.setTakeAt(takeAt);
                alert.setAlertAt(alertAt);
                alert.setEmailSent(false);
                alert.setStatus(MedicationAlertStatus.UNREAD);
                alert.setMessage(buildMessage(schedule, takeAt));
                alert.setCreatedAt(createdAt);

                alertRepository.save(alert);
            }
        }
    }

    private String buildMessage(PatientMedicamentSchedule schedule, LocalDateTime takeAt) {
        return schedule.getMedicament().getName()
                + " - " + schedule.getDose()
                + " at " + takeAt;
    }
}

