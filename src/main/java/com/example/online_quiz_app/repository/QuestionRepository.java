package com.example.online_quiz_app.repository;

import com.example.online_quiz_app.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}