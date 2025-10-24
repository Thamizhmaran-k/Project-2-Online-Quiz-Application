package com.example.online_quiz_app.service;

import com.example.online_quiz_app.model.Role;
import com.example.online_quiz_app.model.User;
import com.example.online_quiz_app.repository.RoleRepository;
import com.example.online_quiz_app.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create ROLE_ADMIN if it doesn't exist
        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);
            System.out.println("--- ROLE_ADMIN created ---");
        }

        // Create ROLE_PARTICIPANT if it doesn't exist
        if (roleRepository.findByName("ROLE_PARTICIPANT").isEmpty()) {
            Role userRole = new Role();
            userRole.setName("ROLE_PARTICIPANT");
            roleRepository.save(userRole);
            System.out.println("--- ROLE_PARTICIPANT created ---");
        }

        // Create an admin user if it doesn't exist
        if (userRepository.findByUsername("admin").isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setEmail("admin@example.com");
            adminUser.setPassword(passwordEncoder.encode("admin")); // Password is 'admin'

            Role adminRole = roleRepository.findByName("ROLE_ADMIN").get();
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            adminUser.setRoles(roles);

            userRepository.save(adminUser);
            System.out.println("--- Admin user created ---");
        }
    }
}