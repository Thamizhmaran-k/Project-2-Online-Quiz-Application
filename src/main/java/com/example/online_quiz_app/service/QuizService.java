package com.example.online_quiz_app.service;

import com.example.online_quiz_app.model.Question;
import com.example.online_quiz_app.model.Quiz;
import com.example.online_quiz_app.repository.QuestionRepository;
import com.example.online_quiz_app.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public Optional<Quiz> getQuizById(Long id) {
        return quizRepository.findById(id);
    }

    public Optional<Quiz> getQuizByIdWithQuestions(Long id) {
        return quizRepository.findByIdWithQuestions(id);
    }

    public Quiz saveQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    public void deleteQuiz(Long id) {
        quizRepository.deleteById(id);
    }

    public Question addQuestionToQuiz(Long quizId, Question question) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        question.setQuiz(quiz);
        return questionRepository.save(question);
    }

    public Optional<Question> getQuestionById(Long questionId) {
         return questionRepository.findById(questionId);
    }

    public void deleteQuestion(Long questionId) {
        questionRepository.deleteById(questionId);
    }
}