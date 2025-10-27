package com.example.online_quiz_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((authorize) ->
                        authorize
                                // Allow password reset pages
                                .requestMatchers("/forgot-password", "/reset-password/**").permitAll()
                                // Allow public access
                                .requestMatchers("/register/**", "/css/**", "/js/**", "/").permitAll()
                                // Admin only
                                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                                // Participant only
                                .requestMatchers("/quiz/**", "/results/**").hasAuthority("ROLE_PARTICIPANT")
                                // All other requests need authentication
                                .anyRequest().authenticated()
                ).formLogin(
                        form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/dashboard", true)
                                .permitAll()
                ).logout(
                        logout -> logout
                                .logoutSuccessUrl("/login?logout")
                                .permitAll()
                );
        return http.build();
    }
}