package com.akgarg.ecommerce.service.email;

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
public class EmailServiceImpl implements EmailService {

    @Value("${email-service.auth-username}")
    private String authUsername;

    @Value("${email-service.auth-password}")
    private String authPassword;

    @Override
    public boolean sendEmail(final String to, final String subject, final String message) {
        boolean isEmailSent = false;

        final Properties properties = System.getProperties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", true);

        final Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(authUsername, authPassword);
            }
        };

        final Session session = Session.getInstance(properties, authenticator);
        final MimeMessage mimeMessage = new MimeMessage(session);

        try {
            mimeMessage.setFrom("akgarg0472.project@gmail.com");
            mimeMessage.setSubject(subject);
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            mimeMessage.setContent(message, "text/html; charset=ISO-8859-1");
            Transport.send(mimeMessage);
            isEmailSent = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isEmailSent;
    }

}
