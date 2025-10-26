package com.example.online_quiz_app.service;

import jakarta.servlet.http.HttpServletRequest; // Required import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // Required import
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder; // Required import
import org.springframework.web.context.request.ServletRequestAttributes; // Required import

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Inject the 'from' email address from application.properties
    @Value("${spring.mail.username}")
    private String senderEmail;

    // General email sending method
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail); // Use the injected value
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
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

    // Helper method to create the full URL dynamically
    private String createResetUrl(String token) {
        try {
            // Get the current request to build the base URL
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String scheme = request.getScheme(); // http or https
                String serverName = request.getServerName(); // localhost or your domain
                int serverPort = request.getServerPort(); // 8080, 80, 443, etc.

                // Construct base URL, omitting default ports 80/443
                String baseUrl = scheme + "://" + serverName;
                if (!((scheme.equals("http") && serverPort == 80) || (scheme.equals("https") && serverPort == 443))) {
                    baseUrl += ":" + serverPort;
                }
                
                return baseUrl + "/reset-password?token=" + token;
            }
        } catch (IllegalStateException e) {
            // This might happen in contexts without a web request (e.g., tests)
            System.err.println("Could not create reset URL: No current request found.");
            // Fallback for non-web contexts - adjust if needed for your environment
            return "http://localhost:8080/reset-password?token=" + token; 
        }
        // Fallback if attributes were null for some reason
        return "http://localhost:8080/reset-password?token=" + token;
    }
}