package com.example.online_quiz_app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

// We removed @Data and are adding the methods manually.
public class UserDto {
    @NotEmpty(message = "Username should not be empty")
    private String username;

    @NotEmpty(message = "Email should not be empty")
    @Email
    private String email;

    @NotEmpty(message = "Password should not be empty")
    private String password;

    // --- MANUALLY ADDED GETTERS AND SETTERS ---

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}