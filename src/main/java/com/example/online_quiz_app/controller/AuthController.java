package com.example.online_quiz_app.controller;

import com.example.online_quiz_app.dto.UserDto;
import com.example.online_quiz_app.model.User;
import com.example.online_quiz_app.service.EmailService;
import com.example.online_quiz_app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired; // Optional but good practice
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority; // Required for dashboard logic
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Required for flash messages

@Controller
public class AuthController {

    private final UserService userService;
    private final EmailService emailService; // Inject EmailService

    // Constructor Injection
    @Autowired // Optional but good practice
    public AuthController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model) {
        // Add a link variable for the template to use
        model.addAttribute("forgotPasswordUrl", "/forgot-password");
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "register";
    }

    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto,
                               BindingResult result, Model model) {
        // Check if username exists
        User existingUserByUsername = userService.findByUsername(userDto.getUsername());
        if (existingUserByUsername != null && existingUserByUsername.getUsername() != null && !existingUserByUsername.getUsername().isEmpty()) {
            result.rejectValue("username", null, "There is already an account registered with that username");
        }
        // Check if email exists
        User existingUserByEmail = userService.findByEmail(userDto.getEmail());
        if (existingUserByEmail != null && existingUserByEmail.getEmail() != null && !existingUserByEmail.getEmail().isEmpty()) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "register"; // Return to registration form if errors
        }

        userService.saveUser(userDto);
        return "redirect:/register?success"; // Redirect on successful registration
    }

    // Corrected dashboard redirect logic
    @GetMapping("/dashboard")
    public String userDashboard(Authentication authentication) {
        // Check if the user has the ROLE_ADMIN authority
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (isAdmin) {
            // If admin, redirect to admin dashboard
            return "redirect:/admin/dashboard";
        } else {
            // Otherwise, redirect to participant quiz list
            return "redirect:/quiz/list";
        }
    }

    // --- Password Reset Endpoints ---

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password"; // Renders forgot-password.html
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String userEmail,
                                        RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userEmail);
        if (user != null) {
            String token = userService.createPasswordResetTokenForUser(user);
            try {
                 // Attempt to send the email
                 emailService.sendPasswordResetEmail(user.getEmail(), token);
                 // Show success message regardless of whether email was found, for security
                 redirectAttributes.addFlashAttribute("message", "If an account with that email exists, a password reset link has been sent.");
            } catch (Exception e) {
                 // Log the error server-side
                 System.err.println("Failed to send password reset email for " + userEmail + ": " + e.getMessage());
                 // Show a generic error to the user
                 redirectAttributes.addFlashAttribute("error", "Could not send reset email at this time. Please try again later.");
            }
        } else {
            // Show the same generic message even if email not found, for security
            redirectAttributes.addFlashAttribute("message", "If an account with that email exists, a password reset link has been sent.");
        }
        return "redirect:/forgot-password"; // Redirect back to the form
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam(value = "token", required = false) String token,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        // Check if token is provided in the URL
        if (token == null || token.isEmpty()) {
             redirectAttributes.addFlashAttribute("error", "Password reset token is missing from the link.");
             return "redirect:/login"; // Redirect if no token
        }

        // Validate the token (checks existence and expiry)
        User user = userService.findByPasswordResetToken(token);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired password reset token. Please request a new one.");
            return "redirect:/login"; // Redirect if token is bad
        }

        // Token is valid, show the form and pass the token to it
        model.addAttribute("token", token);
        return "reset-password"; // Renders reset-password.html
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       RedirectAttributes redirectAttributes) {
        // Re-validate the token on submission
        User user = userService.findByPasswordResetToken(token);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired password reset token.");
            return "redirect:/login";
        }

        // Basic password validation
        if (password == null || password.length() < 6) {
             redirectAttributes.addFlashAttribute("error", "Password must be at least 6 characters long.");
             // Pass token back to the form using query parameter on redirect
             redirectAttributes.addAttribute("token", token);
             return "redirect:/reset-password";
        }

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
            // Pass token back to the form using query parameter on redirect
            redirectAttributes.addAttribute("token", token);
            return "redirect:/reset-password";
        }

        // If all checks pass, change the password
        userService.changeUserPassword(user, password);
        redirectAttributes.addFlashAttribute("message", "Your password has been reset successfully. Please log in.");
        return "redirect:/login"; // Redirect to login page on success
    }
}

