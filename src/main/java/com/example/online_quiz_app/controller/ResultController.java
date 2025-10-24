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

@Controller
public class ResultController {

    @Autowired
    private ResultService resultService;

    @Autowired
    private UserService userService;

    @GetMapping("/results/my")
    public String myResults(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("results", resultService.getResultsForUser(currentUser));
        return "user/my-results";
    }
}