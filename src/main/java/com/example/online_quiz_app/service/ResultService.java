package com.example.online_quiz_app.service;

import com.example.online_quiz_app.model.Question;
import com.example.online_quiz_app.model.Quiz;
import com.example.online_quiz_app.model.Result;
import com.example.online_quiz_app.model.User;
import com.example.online_quiz_app.repository.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ResultService {

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private EmailService emailService;


    public Result saveResult(Quiz quiz, User user, Map<Long, Integer> userAnswers) {
        int correctAnswers = 0;
        int totalQuestions = (quiz.getQuestions() != null) ? quiz.getQuestions().size() : 0;

        if (totalQuestions > 0) {
            for (Question question : quiz.getQuestions()) {
                // Get the user's answer for this question (could be null)
                Integer userAnswerIndex = userAnswers.get(question.getId());

                // Check if the user answered AND if the answer is correct
                if (userAnswerIndex != null && userAnswerIndex.equals(question.getCorrectAnswerIndex())) {
                    correctAnswers++;
                }
            }
        }

        Result result = new Result();
        result.setUser(user);
        result.setQuiz(quiz);
        result.setTotalQuestions(totalQuestions);
        result.setTotalCorrect(correctAnswers);

        // Calculate score, avoid division by zero
        int score = (totalQuestions > 0) ? (int) (((double) correctAnswers / totalQuestions) * 100) : 0;
        result.setScore(score);
        result.setSubmissionTime(LocalDateTime.now());

        Result savedResult = resultRepository.save(result);

        // Send result email
        String subject = "Quiz Result for " + quiz.getTitle();
        String body = "Hi " + user.getUsername() + ",\n\nYou scored " + score + "% on the quiz '" + quiz.getTitle() + "'.\nCorrect Answers: " + correctAnswers + "/" + totalQuestions;
        emailService.sendEmail(user.getEmail(), subject, body);

        return savedResult;
    }

    public List<Result> getResultsForUser(User user) {
        return resultRepository.findByUser(user);
    }
}