package com.akgarg.ecommerce.helper;

import javax.mail.Message;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailHelper {

    public static boolean sendEmail(String to, String subject, String message) {
        boolean isSend = false;

        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", true);

        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("akgarg0472.project@gmail.com", "***************");
            }
        };

        Session session = Session.getInstance(properties, authenticator);
        // session.setDebug(true);

        MimeMessage mimeMessage = new MimeMessage(session);

        try {
            mimeMessage.setFrom("akgarg0472.project@gmail.com");
            mimeMessage.setSubject(subject);
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            mimeMessage.setContent(message, "text/html; charset=ISO-8859-1");
            Transport.send(mimeMessage);
            isSend = true;
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
        }

        return isSend;
    }
}