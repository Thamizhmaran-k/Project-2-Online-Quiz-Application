package com.example.online_quiz_app.repository;

import com.example.online_quiz_app.model.Result;
import com.example.online_quiz_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findByUser(User user);
}
