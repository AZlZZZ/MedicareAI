package tn.esprit.medicareaitigerback.services.implementaions;

import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import tn.esprit.medicareaitigerback.services.interfaces.IMedicamentReminderEmailService;

import java.io.UnsupportedEncodingException;

@Service
@AllArgsConstructor
public class MedicamentReminderEmailService implements IMedicamentReminderEmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Override
    public void sendMedicamentReminder(String to, String medicamentName, String dose, String formattedTakeAt) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            Context context = new Context();
            context.setVariable("medicamentName", medicamentName);
            context.setVariable("dose", dose);
            context.setVariable("takeAt", formattedTakeAt);

            String htmlContent = templateEngine.process("email-medicament-reminder", context);

            helper.setFrom("contact@bazar.tn", "BAZAR");
            helper.setTo(to);
            helper.setSubject("Medication Reminder");
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            // Keep scheduler resilient: log and continue
            throw new RuntimeException("Failed to send medicament reminder email", e);
        }
    }
}

