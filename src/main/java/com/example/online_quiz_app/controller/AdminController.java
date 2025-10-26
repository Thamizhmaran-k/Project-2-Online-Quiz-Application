package com.example.online_quiz_app.controller;

import com.example.online_quiz_app.model.Question;
import com.example.online_quiz_app.model.Quiz;
import com.example.online_quiz_app.service.QuizService;
// --- Imports needed for Swagger/OpenAPI (Optional but included) ---
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
// --- Standard Spring Imports ---
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
// --- Java Util Imports ---
import java.util.ArrayList;
import java.util.List; // Ensure List is imported

@Controller
@RequestMapping("/admin")
@Tag(name = "Admin Management", description = "Endpoints for managing quizzes and questions") // Optional Swagger annotation
public class AdminController {

    @Autowired
    private QuizService quizService;

    @Operation(summary = "Get all quizzes as JSON") // Optional Swagger annotation
    @GetMapping("/api/quizzes")
    @ResponseBody // Make sure this import is present if using REST endpoint
    public List<Quiz> getAllQuizzesApi() {
        return quizService.getAllQuizzes();
    }

    @Operation(summary = "Display the admin dashboard with all quizzes") // Optional Swagger annotation
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("quizzes", quizService.getAllQuizzes());
        return "admin/dashboard";
    }

    @Operation(summary = "Show the form to add a new quiz") // Optional Swagger annotation
    @GetMapping("/quiz/add")
    public String showAddQuizForm(Model model) {
        model.addAttribute("quiz", new Quiz());
        return "admin/quiz-form";
    }

    @Operation(summary = "Save a new quiz") // Optional Swagger annotation
    @PostMapping("/quiz/add")
    public String addQuiz(@ModelAttribute Quiz quiz) {
        // Ensure questions list is initialized if needed for new quizzes
        if (quiz.getQuestions() == null) {
             quiz.setQuestions(new ArrayList<>());
        }
        quizService.saveQuiz(quiz);
        return "redirect:/admin/dashboard";
    }

    @Operation(summary = "Show form to edit an existing quiz") // Optional Swagger annotation
    @GetMapping("/quiz/edit/{id}")
    public String showEditQuizForm(@PathVariable Long id, Model model) {
        // Fetch the quiz *with questions* to ensure relationships are loaded
        Quiz quiz = quizService.getQuizByIdWithQuestions(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid quiz Id:" + id));
        model.addAttribute("quiz", quiz);
        return "admin/quiz-form";
    }

    @Operation(summary = "Update an existing quiz") // Optional Swagger annotation
    @PostMapping("/quiz/edit/{id}")
    public String updateQuiz(@PathVariable Long id,
                             @ModelAttribute Quiz formQuiz, // Data from the form
                             RedirectAttributes redirectAttributes) {

        // --- CORRECTED UPDATE LOGIC ---
        // 1. Fetch the EXISTING quiz from the database (including questions)
        Quiz existingQuiz = quizService.getQuizByIdWithQuestions(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid quiz Id:" + id));

        // 2. Update fields from the form onto the existing entity
        existingQuiz.setTitle(formQuiz.getTitle());
        existingQuiz.setDescription(formQuiz.getDescription());
        existingQuiz.setTimePerQuestionInSeconds(formQuiz.getTimePerQuestionInSeconds());
        // We explicitly DO NOT touch the 'questions' list here

        // 3. Save the updated existing entity
        quizService.saveQuiz(existingQuiz);

        redirectAttributes.addFlashAttribute("successMessage", "Quiz updated successfully!");
        return "redirect:/admin/dashboard";
    }

    @Operation(summary = "Delete a quiz") // Optional Swagger annotation
    @GetMapping("/quiz/delete/{id}")
    public String deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return "redirect:/admin/dashboard";
    }

    @Operation(summary = "View questions for a specific quiz") // Optional Swagger annotation
    @GetMapping("/quiz/{quizId}/questions")
    public String viewQuizQuestions(@PathVariable Long quizId, Model model) {
        // Fetch with questions needed here to display them
        Quiz quiz = quizService.getQuizByIdWithQuestions(quizId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid quiz Id:" + quizId));
        model.addAttribute("quiz", quiz);
        return "admin/question-list";
    }

    @Operation(summary = "Show form to add a question to a quiz") // Optional Swagger annotation
    @GetMapping("/quiz/{quizId}/question/add")
    public String showAddQuestionForm(@PathVariable Long quizId, Model model) {
        Question question = new Question();
        question.setOptions(new ArrayList<>()); // Initialize options
        for (int i = 0; i < 4; i++) { // Assuming 4 options
            question.getOptions().add("");
        }
        model.addAttribute("question", question);
        model.addAttribute("quizId", quizId);
        return "admin/question-form";
    }

    @Operation(summary = "Save a new question for a quiz") // Optional Swagger annotation
    @PostMapping("/quiz/{quizId}/question/add")
    public String addQuestion(@PathVariable Long quizId, @ModelAttribute Question question, RedirectAttributes redirectAttributes) {
        quizService.addQuestionToQuiz(quizId, question);
        redirectAttributes.addFlashAttribute("successMessage", "Question added successfully!");
        return "redirect:/admin/quiz/" + quizId + "/questions";
    }

    @Operation(summary = "Delete a question") // Optional Swagger annotation
    @GetMapping("/question/delete/{questionId}")
    public String deleteQuestion(@PathVariable Long questionId, RedirectAttributes redirectAttributes) {
        // Need to fetch question to find its quiz ID before deleting
        Question question = quizService.getQuestionById(questionId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid question Id:" + questionId));
        Long quizId = question.getQuiz().getId(); // Get Quiz ID *before* deleting

        quizService.deleteQuestion(questionId);

        redirectAttributes.addFlashAttribute("successMessage", "Question deleted successfully!");
        return "redirect:/admin/quiz/" + quizId + "/questions"; // Redirect back to the question list for the same quiz
    }
}
