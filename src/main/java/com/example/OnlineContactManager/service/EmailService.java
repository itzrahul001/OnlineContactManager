package com.example.OnlineContactManager.service;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService {

    public boolean sendEmail(String subject, String message, String to) {
        boolean isSent = false;

        // Sender's email
        String from = "rahulydtest@gmail.com";
        // Gmail SMTP server
        String host = "smtp.gmail.com";

        // Setup mail server properties
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Create a new session with an authenticator
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // Replace with your actual email and app password
                return new PasswordAuthentication("email@gmail.com", "password");
            }
        });

        session.setDebug(true); // Enable debug mode for troubleshooting

        try {
            // Create a MimeMessage
            MimeMessage mimeMessage = new MimeMessage(session);

            // Set sender
            mimeMessage.setFrom(from);

            // Set recipient
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set subject
            mimeMessage.setSubject(subject);

            // Set email body
            //mimeMessage.setText(message);
            mimeMessage.setContent(message, "text/html");

            // Send email
            Transport.send(mimeMessage);

            System.out.println("Email sent successfully...");
            isSent = true;

        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return isSent;
    }
}