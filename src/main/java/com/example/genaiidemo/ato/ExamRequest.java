package com.example.genaiidemo.ato;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExamRequest {
    private String email;
    private String subject;
    private String gradeLevel;
    private String examType;
    private String difficulty;
    private List<String> topics;
    private int marks1;
    private int marks2;
    private int marks5;
}
