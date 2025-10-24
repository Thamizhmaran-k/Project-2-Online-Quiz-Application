package com.example.online_quiz_app.controller;

import com.example.online_quiz_app.model.Question;
import com.example.online_quiz_app.model.Quiz;
import com.example.online_quiz_app.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private QuizService quizService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("quizzes", quizService.getAllQuizzes());
        return "admin/dashboard";
    }

    @GetMapping("/quiz/add")
    public String showAddQuizForm(Model model) {
        model.addAttribute("quiz", new Quiz());
        return "admin/quiz-form";
    }

    @PostMapping("/quiz/add")
    public String addQuiz(@ModelAttribute Quiz quiz) {
        quizService.saveQuiz(quiz);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/quiz/edit/{id}")
    public String showEditQuizForm(@PathVariable Long id, Model model) {
        Quiz quiz = quizService.getQuizById(id).orElseThrow(() -> new IllegalArgumentException("Invalid quiz Id:" + id));
        model.addAttribute("quiz", quiz);
        return "admin/quiz-form";
    }

    @PostMapping("/quiz/edit/{id}")
    public String updateQuiz(@PathVariable Long id, @ModelAttribute Quiz quiz) {
        quiz.setId(id); // Ensure the ID is set for update
        quizService.saveQuiz(quiz);
        return "redirect:/admin/dashboard";
    }


    @GetMapping("/quiz/delete/{id}")
    public String deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/quiz/{quizId}/questions")
    public String viewQuizQuestions(@PathVariable Long quizId, Model model) {
        Quiz quiz = quizService.getQuizById(quizId).orElseThrow(() -> new IllegalArgumentException("Invalid quiz Id:" + quizId));
        model.addAttribute("quiz", quiz);
        return "admin/question-list";
    }

    @GetMapping("/quiz/{quizId}/question/add")
    public String showAddQuestionForm(@PathVariable Long quizId, Model model) {
        Question question = new Question();
        // Initialize options list to avoid null pointer in Thymeleaf
        question.setOptions(new ArrayList<>()); 
        for (int i = 0; i < 4; i++) { // Assuming 4 options
            question.getOptions().add("");
        }
        model.addAttribute("question", question);
        model.addAttribute("quizId", quizId);
        return "admin/question-form";
    }

    @PostMapping("/quiz/{quizId}/question/add")
    public String addQuestion(@PathVariable Long quizId, @ModelAttribute Question question, RedirectAttributes redirectAttributes) {
        quizService.addQuestionToQuiz(quizId, question);
        redirectAttributes.addFlashAttribute("successMessage", "Question added successfully!");
        return "redirect:/admin/quiz/" + quizId + "/questions";
    }

    @GetMapping("/question/delete/{questionId}")
    public String deleteQuestion(@PathVariable Long questionId, RedirectAttributes redirectAttributes) {
        Question question = quizService.getQuestionById(questionId).orElseThrow(() -> new IllegalArgumentException("Invalid question Id:" + questionId));
        Long quizId = question.getQuiz().getId();
        quizService.deleteQuestion(questionId);
        redirectAttributes.addFlashAttribute("successMessage", "Question deleted successfully!");
        return "redirect:/admin/quiz/" + quizId + "/questions";
    }
}