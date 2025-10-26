package com.example.online_quiz_app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString; // Import ToString
import java.util.List;

@Data
@Entity
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    
    @Column(columnDefinition = "integer default 30")
    private int timePerQuestionInSeconds = 30; 

    // Add ToString.Exclude here
    @ToString.Exclude 
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions;
}