package com.example.online_quiz_app.controller;

import com.example.online_quiz_app.config.SecurityConfig;
import com.example.online_quiz_app.model.User; // <<< REQUIRED IMPORT
import com.example.online_quiz_app.security.CustomUserDetailsService;
import com.example.online_quiz_app.service.EmailService;
import com.example.online_quiz_app.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

// Static imports for MockMvc and Mockito
import static org.mockito.Mockito.when; // <<< REQUIRED IMPORT
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    // ... testShowLoginPage, testShowRegistrationForm, testShowForgotPasswordForm methods ...

    @Test
    void testShowResetPasswordForm_withValidToken() throws Exception {
         // --- ARRANGE ---
         // Tell the mock userService to return a User when findByPasswordResetToken is called with "validToken"
         when(userService.findByPasswordResetToken("validToken")).thenReturn(new User()); // <<< THIS LINE IS NOW UNCOMMENTED

         // --- ACT & ASSERT ---
         mockMvc.perform(get("/reset-password").param("token", "validToken"))
                .andExpect(status().isOk()) // Should now return 200 OK
                .andExpect(view().name("reset-password"))
                .andExpect(model().attribute("token", "validToken"));
    }

     @Test
    void testShowResetPasswordForm_withInvalidToken() throws Exception {
         // Arrange: Mock userService to return null (which it does by default)
         // when(userService.findByPasswordResetToken("invalidToken")).thenReturn(null); // No need to explicitly mock for null

         // Act & Assert
         mockMvc.perform(get("/reset-password").param("token", "invalidToken"))
                .andExpect(status().is3xxRedirection()) // Expect a redirect
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("error")); // Expect an error message
    }
}
