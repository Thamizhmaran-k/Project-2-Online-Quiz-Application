package com.example.online_quiz_app.controller;

import com.example.online_quiz_app.model.Quiz;
import com.example.online_quiz_app.service.QuizService;
import com.example.online_quiz_app.service.ResultService;
import com.example.online_quiz_app.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuizController.class)
public class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuizService quizService;
    @MockBean
    private ResultService resultService;
    @MockBean
    private UserService userService;

    @MockBean
    private com.example.online_quiz_app.security.CustomUserDetailsService customUserDetailsService;


    @Test
    @WithMockUser(authorities = "ROLE_PARTICIPANT")
    public void testListQuizzes() throws Exception {
        Quiz quiz1 = new Quiz();
        List<Quiz> mockQuizList = List.of(quiz1);
        when(quizService.getAllQuizzes()).thenReturn(mockQuizList);

        mockMvc.perform(get("/quiz/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/quiz-list"))
                .andExpect(model().attribute("quizzes", hasSize(1)));
    }

    @Test
    @WithMockUser(authorities = "ROLE_PARTICIPANT")
    public void testTakeQuiz() throws Exception {
        Quiz mockQuiz = new Quiz();
        mockQuiz.setId(1L);
        when(quizService.getQuizByIdWithQuestions(1L)).thenReturn(Optional.of(mockQuiz));

        mockMvc.perform(get("/quiz/take/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/quiz-take"))
                .andExpect(model().attribute("quiz", mockQuiz));
    }
}