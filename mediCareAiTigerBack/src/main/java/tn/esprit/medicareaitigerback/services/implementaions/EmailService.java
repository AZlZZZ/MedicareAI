package tn.esprit.medicareaitigerback.services.implementaions;

import tn.esprit.medicareaitigerback.repositories.PatientRepository;
import tn.esprit.medicareaitigerback.services.interfaces.IEmailService;
import tn.esprit.medicareaitigerback.utilities.Utils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;


@Service
@AllArgsConstructor
public class EmailService implements IEmailService {
    @Autowired
    private JavaMailSender mailSender;
    private PatientRepository patientRepository;
    private TemplateEngine templateEngine;
    private Utils utils;

    @Override
    public void sendResetEmail(String to, String resetLink) throws MessagingException, UnsupportedEncodingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        var client = patientRepository.findClientByEmail(to);

        Context context = new Context();
        context.setVariable("clientName", client.get().getFirstName());
        context.setVariable("resetLink", resetLink);

        String htmlContent = templateEngine.process("email-reset-password", context);
        helper.setFrom("contact@bazar.tn", "BAZAR");
        helper.setTo(client.get().getEmail());
        helper.setSubject("Password Reset Request");
        helper.setText(htmlContent, true);

        mailSender.send(mimeMessage);
        //mailSender.send(message);

    }

    @Override
    public void sendResetEmailAgent(String to, String resetLink) throws MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        Context context = new Context();
        //context.setVariable("agentName",  name);
        context.setVariable("resetLink", resetLink);

        String htmlContent = templateEngine.process("email-reset-password", context);
        helper.setFrom("contact@bazar.tn", "BAZAR");
        helper.setTo(to);
        helper.setSubject("Reset Your BAZAR Account Password");
        helper.setText(htmlContent, true);
        mailSender.send(mimeMessage);
    }
}

