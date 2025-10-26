package com.example.online_quiz_app.service;

import com.example.online_quiz_app.model.Question;
import com.example.online_quiz_app.model.Quiz;
import com.example.online_quiz_app.repository.QuestionRepository;
import com.example.online_quiz_app.repository.QuizRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuizService quizService;

    @Test
    void testGetAllQuizzes() {
        Quiz quiz1 = new Quiz();
        quiz1.setId(1L);
        quiz1.setTitle("Java Basics");
        List<Quiz> mockQuizList = List.of(quiz1);

        when(quizRepository.findAll()).thenReturn(mockQuizList);

        List<Quiz> result = quizService.getAllQuizzes();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testAddQuestionToQuiz() {
        Quiz mockQuiz = new Quiz();
        mockQuiz.setId(1L);
        Question questionToAdd = new Question();
        questionToAdd.setContent("What is Spring Boot?");

        when(quizRepository.findById(1L)).thenReturn(Optional.of(mockQuiz));
        when(questionRepository.save(any(Question.class))).thenReturn(questionToAdd);

        Question savedQuestion = quizService.addQuestionToQuiz(1L, questionToAdd);

        assertNotNull(savedQuestion);
        assertEquals(mockQuiz, savedQuestion.getQuiz());
        verify(questionRepository).save(questionToAdd);
    }
}