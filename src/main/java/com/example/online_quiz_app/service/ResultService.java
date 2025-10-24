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
        for (Question question : quiz.getQuestions()) {
            Integer userAnswerIndex = userAnswers.get(question.getId());
            if (userAnswerIndex != null && userAnswerIndex == question.getCorrectAnswerIndex()) {
                correctAnswers++;
            }
        }

        Result result = new Result();
        result.setUser(user);
        result.setQuiz(quiz);
        result.setTotalQuestions(quiz.getQuestions().size());
        result.setTotalCorrect(correctAnswers);
        int score = (int) (((double) correctAnswers / quiz.getQuestions().size()) * 100);
        result.setScore(score);
        result.setSubmissionTime(LocalDateTime.now());

        Result savedResult = resultRepository.save(result);

        // Send result email
        String subject = "Quiz Result for " + quiz.getTitle();
        String body = "Hi " + user.getUsername() + ",\n\nYou scored " + score + "% on the quiz '" + quiz.getTitle() + "'.\nCorrect Answers: " + correctAnswers + "/" + quiz.getQuestions().size();
        emailService.sendEmail(user.getEmail(), subject, body);

        return savedResult;
    }

    public List<Result> getResultsForUser(User user) {
        return resultRepository.findByUser(user);
    }
}