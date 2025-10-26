package com.example.online_quiz_app.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // General email sending method
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom("thamizhmaran0@gmail.com");

            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
        } catch (Exception e) {
            // Log the error including the recipient for easier debugging
            System.err.println("Error sending email to " + to + ": " + e.getMessage());
        }
    }

    // Specific method for password reset emails
    public void sendPasswordResetEmail(String recipientEmail, String token) {
        // Build the full reset URL (handles http/https, server name, port)
        String resetUrl = createResetUrl(token);

        String subject = "Reset Your Password - Online Quiz App";
        String body = "To reset your password, click the link below:\n"
                    + resetUrl
                    + "\n\nThis link is valid for 1 hour."
                    + "\nIf you did not request a password reset, please ignore this email.";

        sendEmail(recipientEmail, subject, body);
    }

    private String createResetUrl(String token) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String scheme = request.getScheme();
                String serverName = request.getServerName();
                int serverPort = request.getServerPort();

                String baseUrl = scheme + "://" + serverName;
                if (!((scheme.equals("http") && serverPort == 80) || (scheme.equals("https") && serverPort == 443))) {
                    baseUrl += ":" + serverPort;
                }

                return baseUrl + "/reset-password?token=" + token;
            }
        } catch (IllegalStateException e) {
            System.err.println("Could not create reset URL dynamically: No current request found. Using fallback.");
            return "http://localhost:8080/reset-password?token=" + token;
        }
        
        System.err.println("Could not create reset URL dynamically: Request attributes were null. Using fallback.");
        return "http://localhost:8080/reset-password?token=" + token;
    }
}

