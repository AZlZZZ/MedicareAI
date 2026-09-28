package tn.esprit.medicareaitigerback.services.interfaces;

import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface IEmailService {
    void sendResetEmailAgent(String to, String resetLink) throws MessagingException, UnsupportedEncodingException;

    void sendResetEmail(String to, String resetLink) throws MessagingException, UnsupportedEncodingException;

}
