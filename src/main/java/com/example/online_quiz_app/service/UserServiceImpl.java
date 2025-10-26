package com.example.online_quiz_app.service;

import com.example.online_quiz_app.dto.UserDto;
import com.example.online_quiz_app.model.Role;
import com.example.online_quiz_app.model.User;
import com.example.online_quiz_app.repository.RoleRepository;
import com.example.online_quiz_app.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime; // Required import
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID; // Required import

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // Constructor Injection
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public void saveUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Role participantRole = roleRepository.findByName("ROLE_PARTICIPANT")
            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        Set<Role> roles = new HashSet<>();
        roles.add(participantRole);
        user.setRoles(roles);

        userRepository.save(user);

        String subject = "Registration Successful!";
        String body = "Welcome, " + user.getUsername() + "! Your registration for the Online Quiz App was successful.";
        emailService.sendEmail(user.getEmail(), subject, body);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public String createPasswordResetTokenForUser(User user) {
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1)); // Token valid for 1 hour
        userRepository.save(user);
        return token;
    }

    @Override
    public User findByPasswordResetToken(String token) {
        return userRepository.findByResetToken(token)
                // Check if token exists AND is not expired
                .filter(u -> u.getResetTokenExpiry() != null && u.getResetTokenExpiry().isAfter(LocalDateTime.now()))
                .orElse(null);
    }

    @Override
    public void changeUserPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        user.setResetToken(null); // Invalidate token after use
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }
}