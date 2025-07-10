package com.example.genaiidemo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String text;

    private String topic;
    private String difficulty;
    private String taxonomyLevel;

    @Lob
    private String answerKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    @JsonBackReference
    private Exam exam;

    // ✅ Required no-arg constructor
    public Question() {}

    // ✅ Optional constructor for PDF/AI-based creation
    public Question(String text, String markType, String difficulty, Exam exam) {
        this.text = text;
        this.topic = "N/A"; // or extract from context
        this.difficulty = difficulty;
        this.taxonomyLevel = "understanding";
        this.answerKey = "";
        this.exam = exam;
    }

    // ✅ All Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getTaxonomyLevel() { return taxonomyLevel; }
    public void setTaxonomyLevel(String taxonomyLevel) { this.taxonomyLevel = taxonomyLevel; }

    public String getAnswerKey() { return answerKey; }
    public void setAnswerKey(String answerKey) { this.answerKey = answerKey; }

    public Exam getExam() { return exam; }
    public void setExam(Exam exam) { this.exam = exam; }
    public Question(String text, String taxonomyLevel, String topic, String difficulty, Exam exam) {
        this.text = text;
        this.taxonomyLevel = taxonomyLevel;
        this.topic = topic;
        this.difficulty = difficulty;
        this.exam = exam;
    }

}
