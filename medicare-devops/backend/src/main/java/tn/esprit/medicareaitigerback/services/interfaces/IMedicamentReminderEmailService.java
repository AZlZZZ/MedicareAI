package tn.esprit.medicareaitigerback.services.interfaces;

public interface IMedicamentReminderEmailService {
    void sendMedicamentReminder(String to, String medicamentName, String dose, String formattedTakeAt);
}

