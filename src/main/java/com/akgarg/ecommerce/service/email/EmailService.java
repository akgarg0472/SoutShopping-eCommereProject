package com.akgarg.ecommerce.service.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @author Akhilesh Garg
 * @since 21-10-2022
 */
@Service
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private final Properties properties;
    private final Authenticator authenticator;
    private final String senderEmail;

    public EmailService(
            @Value("${email.auth-username}") final String authUsername,
            @Value("${email.auth-password}") final String authPassword
    ) {
        this.properties = System.getProperties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", true);

        this.senderEmail = authUsername;

        this.authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(authUsername, authPassword);
            }
        };
    }

    public boolean sendEmail(final String to, final String subject, final String message) {
        LOGGER.info("Sending email: to: {}, subject: {}", to, subject);
        boolean isEmailSent = false;

        final Session session = Session.getInstance(properties, authenticator);
        final MimeMessage mimeMessage = new MimeMessage(session);

        try {
            mimeMessage.setFrom(senderEmail);
            mimeMessage.setSubject(subject);
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            mimeMessage.setContent(message, "text/html; charset=ISO-8859-1");
            Transport.send(mimeMessage);
            isEmailSent = true;
        } catch (Exception e) {
            LOGGER.error("Error sending email", e);
        }

        return isEmailSent;
    }

}
