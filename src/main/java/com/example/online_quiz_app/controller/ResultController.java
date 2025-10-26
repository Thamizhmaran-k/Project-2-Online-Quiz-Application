package com.example.online_quiz_app.controller;

import com.example.online_quiz_app.model.User;
import com.example.online_quiz_app.service.ResultService;
import com.example.online_quiz_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping; // Optional: Add if needed

import java.util.List; // Import List

@Controller
@RequestMapping("/results") // Group result-related mappings
public class ResultController {

    @Autowired
    private ResultService resultService;

    @Autowired
    private UserService userService;

    @GetMapping("/my")
    public String myResults(Model model, Authentication authentication) {
        System.out.println("Fetching results for current user."); // DEBUG LOG
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userService.findByEmail(userDetails.getUsername()); // Use findByEmail

        if (currentUser == null) {
            // Handle case where user is not found (shouldn't happen if logged in)
            System.err.println("Error: Could not find user: " + userDetails.getUsername());
            return "redirect:/login?error"; // Redirect to login
        }

        List<com.example.online_quiz_app.model.Result> userResults = resultService.getResultsForUser(currentUser);
        model.addAttribute("results", userResults);
        System.out.println("Found " + userResults.size() + " results for user."); // DEBUG LOG
        return "user/my-results"; // Ensure this template exists and is correct
    }
}