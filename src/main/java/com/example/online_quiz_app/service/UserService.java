package com.example.online_quiz_app.service;

import com.example.online_quiz_app.dto.UserDto;
import com.example.online_quiz_app.model.User; // Required import

public interface UserService {
    void saveUser(UserDto userDto);
    User findByUsername(String username);
    User findByEmail(String email);
    String createPasswordResetTokenForUser(User user); // New method
    User findByPasswordResetToken(String token); // New method
    void changeUserPassword(User user, String password); // New method
}