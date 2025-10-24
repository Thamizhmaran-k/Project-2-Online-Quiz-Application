package com.example.online_quiz_app.service;

import com.example.online_quiz_app.dto.UserDto;
import com.example.online_quiz_app.model.User;

public interface UserService {
    void saveUser(UserDto userDto);
    User findByUsername(String username);
    User findByEmail(String email);
}