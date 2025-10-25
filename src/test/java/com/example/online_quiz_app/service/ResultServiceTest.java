package com.example.online_quiz_app.service;

import com.example.online_quiz_app.model.Question;
import com.example.online_quiz_app.model.Quiz;
import com.example.online_quiz_app.model.Result;
import com.example.online_quiz_app.model.User;
import com.example.online_quiz_app.repository.ResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResultServiceTest {

    @Mock
    private ResultRepository resultRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ResultService resultService;

    private User testUser;
    private Quiz testQuiz;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        Question q1 = new Question();
        q1.setId(101L);
        q1.setCorrectAnswerIndex(1);

        Question q2 = new Question();
        q2.setId(102L);
        q2.setCorrectAnswerIndex(2);

        testQuiz = new Quiz();
        testQuiz.setId(1L);
        testQuiz.setQuestions(List.of(q1, q2));
    }

    @Test
    void testSaveResult_CalculatesScoreCorrectly() {
        Map<Long, Integer> userAnswers = Map.of(
                101L, 1, // Correct
                102L, 0  // Incorrect
        );

        when(resultRepository.save(any(Result.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Result result = resultService.saveResult(testQuiz, testUser, userAnswers);

        assertEquals(2, result.getTotalQuestions());
        assertEquals(1, result.getTotalCorrect());
        assertEquals(50, result.getScore());
    }
}