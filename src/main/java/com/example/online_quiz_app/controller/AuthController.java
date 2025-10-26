package com.example.online_quiz_app.controller;

import com.example.online_quiz_app.dto.UserDto;
import com.example.online_quiz_app.model.User;
import com.example.online_quiz_app.service.EmailService; // Required import
import com.example.online_quiz_app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired; // Optional but good practice
import org.springframework.security.core.Authentication;
// import org.springframework.security.core.GrantedAuthority; // Removed as unused
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Required import

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
        // Add a link variable for the template
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
        User existingUserByUsername = userService.findByUsername(userDto.getUsername());
        if (existingUserByUsername != null && existingUserByUsername.getUsername() != null && !existingUserByUsername.getUsername().isEmpty()) {
            result.rejectValue("username", null, "There is already an account registered with that username");
        }
        User existingUserByEmail = userService.findByEmail(userDto.getEmail());
        if (existingUserByEmail != null && existingUserByEmail.getEmail() != null && !existingUserByEmail.getEmail().isEmpty()) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "register";
        }

        userService.saveUser(userDto);
        return "redirect:/register?success";
    }

    @GetMapping("/dashboard")
    public String userDashboard(Authentication authentication) {
        // Logic remains the same...
        return "redirect:/quiz/list"; // Default redirect
    }

    // --- Password Reset Endpoints ---

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String userEmail,
                                        RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userEmail);
        if (user != null) {
            String token = userService.createPasswordResetTokenForUser(user);
            try {
                 emailService.sendPasswordResetEmail(user.getEmail(), token);
                 redirectAttributes.addFlashAttribute("message", "Check your email for a password reset link.");
            } catch (Exception e) {
                 System.err.println("Failed to send password reset email for " + userEmail + ": " + e.getMessage());
                 redirectAttributes.addFlashAttribute("error", "Could not send reset email. Please try again later.");
            }
        } else {
            // Show a generic message even if email not found for security
            redirectAttributes.addFlashAttribute("message", "If an account with that email exists, a reset link has been sent.");
        }
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam(value = "token", required = false) String token,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        if (token == null || token.isEmpty()) {
             redirectAttributes.addFlashAttribute("error", "Password reset token is missing.");
             return "redirect:/login";
        }
        
        User user = userService.findByPasswordResetToken(token);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired password reset token.");
            return "redirect:/login";
        }
        
        model.addAttribute("token", token); // Pass token to the view
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       RedirectAttributes redirectAttributes) {
        User user = userService.findByPasswordResetToken(token);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired password reset token.");
            return "redirect:/login";
        }

        if (password == null || password.length() < 6) {
             redirectAttributes.addFlashAttribute("error", "Password must be at least 6 characters long.");
             redirectAttributes.addAttribute("token", token); // Pass token back via query param
             return "redirect:/reset-password";
        }

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
            redirectAttributes.addAttribute("token", token); // Pass token back via query param
            return "redirect:/reset-password";
        }

        userService.changeUserPassword(user, password);
        redirectAttributes.addFlashAttribute("message", "Your password has been reset successfully. Please log in.");
        return "redirect:/login";
    }
}