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
                             @RequestParam Map<String, String> submittedAnswers,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userService.findByEmail(userDetails.getUsername());
        Quiz quiz = quizService.getQuizByIdWithQuestions(quizId).orElseThrow(() -> new IllegalArgumentException("Invalid quiz Id:" + quizId));

        java.util.Map<Long, Integer> userAnswers = new java.util.HashMap<>();
        submittedAnswers.forEach((key, value) -> {
            if (key.startsWith("question_")) {
                Long questionId = Long.parseLong(key.substring(9));
                if (value != null && !value.isEmpty()) {
                    Integer answerIndex = Integer.parseInt(value);
                    userAnswers.put(questionId, answerIndex);
                }
            }
        });

        Result result = resultService.saveResult(quiz, currentUser, userAnswers);

        redirectAttributes.addFlashAttribute("result", result);
        return "redirect:/quiz/result";
    }

    @GetMapping("/result")
    public String showResult(Model model) {
        if (!model.containsAttribute("result")) {
            return "redirect:/quiz/list";
        }
        return "user/quiz-result";
    }
}