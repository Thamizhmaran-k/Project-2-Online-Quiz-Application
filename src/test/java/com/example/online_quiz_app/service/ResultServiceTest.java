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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultServiceTest {

    @Mock
    private ResultRepository resultRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ResultService resultService;

    private User testUser;
    private Quiz testQuiz;
    private Question q1;
    private Question q2;

    @BeforeEach
    void setUp() {
        // 1. Arrange: Set up all the mock data we need for the test
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        q1 = new Question();
        q1.setId(101L);
        q1.setContent("What is 1+1?");
        q1.setOptions(Arrays.asList("1", "2", "3"));
        q1.setCorrectAnswerIndex(1); // Correct answer is "2"

        q2 = new Question();
        q2.setId(102L);
        q2.setContent("What is the capital of France?");
        q2.setOptions(Arrays.asList("London", "Berlin", "Paris"));
        q2.setCorrectAnswerIndex(2); // Correct answer is "Paris"

        testQuiz = new Quiz();
        testQuiz.setId(1L);
        testQuiz.setTitle("Test Quiz");
        testQuiz.setQuestions(Arrays.asList(q1, q2));
    }

    @Test
    void testSaveResult_CalculatesScoreCorrectly() {
        // Arrange: Define the user's answers (1 correct, 1 incorrect)
        Map<Long, Integer> userAnswers = new HashMap<>();
        userAnswers.put(101L, 1); // Correct answer for Q1
        userAnswers.put(102L, 0); // Incorrect answer for Q2

        // Mock the repository's save method to return what it receives
        when(resultRepository.save(any(Result.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. Act: Call the method we want to test
        Result result = resultService.saveResult(testQuiz, testUser, userAnswers);

        // 3. Assert: Check if the result is what we expect (1 out of 2 correct = 50%)
        assertEquals(2, result.getTotalQuestions());
        assertEquals(1, result.getTotalCorrect());
        assertEquals(50, result.getScore());
    }
}