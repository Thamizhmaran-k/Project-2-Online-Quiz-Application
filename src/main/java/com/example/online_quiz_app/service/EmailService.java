package com.example.online_quiz_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            // Set the 'from' address. Must match the authenticated user in application.properties
            message.setFrom("your_email@gmail.com"); 

            mailSender.send(message);
        } catch (Exception e) {
            // Log the exception, but don't block the user flow
            System.err.println("Error sending email: " + e.getMessage());
        }
    }
}