package com.example.genaiidemo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity // Marks this class as a JPA entity (table)
@Getter  // Lombok: generates getters
@Setter  // Lombok: generates setters
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremented ID
    private Long id;

    private String subject;
    private String gradeLevel;
    private String examType;

    private LocalDateTime createdAt;

    // ⭐ Default values for rating (optional but recommended)
    private Double averageRating = 0.0;
    private Integer ratingCount = 0;

    // 🔁 Many exams belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 🔁 One exam has many questions
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Question> questions;
    public Exam(String subject, String gradeLevel, String examType, LocalDateTime createdAt, User user, List<Question> questions) {
        this.subject = subject;
        this.gradeLevel = gradeLevel;
        this.examType = examType;
        this.createdAt = createdAt;
        this.user = user;
        this.questions = questions;
    }
    public Exam(){

    }
}
