package com.example.online_quiz_app.controller;

import com.example.online_quiz_app.model.Quiz;
import com.example.online_quiz_app.model.Result;
import com.example.online_quiz_app.model.User;
import com.example.online_quiz_app.service.QuizService;
import com.example.online_quiz_app.service.ResultService;
import com.example.online_quiz_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap; // Use HashMap
import java.util.Map;

@Controller
@RequestMapping("/quiz")
public class QuizController {
    @Autowired
    private QuizService quizService;

    @Autowired
    private ResultService resultService;

    @Autowired
    private UserService userService;

    // ... (listQuizzes, takeQuiz methods remain the same) ...
     @GetMapping("/list")
    public String listQuizzes(Model model) {
        model.addAttribute("quizzes", quizService.getAllQuizzes());
        return "user/quiz-list";
    }

    @GetMapping("/take/{id}")
    public String takeQuiz(@PathVariable Long id, Model model) {
        Quiz quiz = quizService.getQuizByIdWithQuestions(id).orElseThrow(() -> new IllegalArgumentException("Invalid quiz Id:" + id));
        model.addAttribute("quiz", quiz);
        return "user/quiz-take";
    }


    @PostMapping("/submit")
    public String submitQuiz(@RequestParam("quizId") Long quizId,
                             @RequestParam Map<String, String> submittedAnswers, // Raw answers from form
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {

        System.out.println("Received submission for quizId: " + quizId); // DEBUG LOG
        System.out.println("Submitted raw answers: " + submittedAnswers); // DEBUG LOG

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userService.findByEmail(userDetails.getUsername());
        Quiz quiz = quizService.getQuizByIdWithQuestions(quizId).orElseThrow(() -> new IllegalArgumentException("Invalid quiz Id:" + quizId));

        // Convert submitted answers to Map<Long, Integer>, handling null/empty for unanswered
        Map<Long, Integer> userAnswers = new HashMap<>(); // Use specific type
        submittedAnswers.forEach((key, value) -> {
            if (key.startsWith("question_")) {
                try {
                    Long questionId = Long.parseLong(key.substring(9));
                    Integer answerIndex = null; // Default to null if not answered
                    // Check if value is present and not empty before parsing
                    if (value != null && !value.trim().isEmpty()) {
                        answerIndex = Integer.parseInt(value);
                    }
                    userAnswers.put(questionId, answerIndex); // Put null if not answered
                } catch (NumberFormatException e) {
                     System.err.println("Error parsing question ID or answer index for key: " + key + ", value: " + value);
                     // Decide how to handle this - maybe skip this answer or treat as incorrect
                     // For now, let's skip it
                }
            }
        });

        System.out.println("Parsed user answers: " + userAnswers); // DEBUG LOG

        Result result = resultService.saveResult(quiz, currentUser, userAnswers);

        System.out.println("Result saved: " + result); // DEBUG LOG

        redirectAttributes.addFlashAttribute("result", result);
        return "redirect:/quiz/result"; // Redirect to the GET mapping
    }

    @GetMapping("/result")
    public String showResult(Model model) {
         // This logic is fine, it displays the result passed via flash attribute
        if (!model.containsAttribute("result")) {
             System.out.println("No result found in flash attributes, redirecting to list."); // DEBUG LOG
            return "redirect:/quiz/list";
        }
        System.out.println("Displaying result page."); // DEBUG LOG
        return "user/quiz-result";
    }
}