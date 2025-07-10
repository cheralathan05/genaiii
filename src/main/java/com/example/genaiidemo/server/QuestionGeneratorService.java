package com.example.genaiidemo.server;

public interface QuestionGeneratorService {
    String generateQuestion(String topic, String difficulty);
    String generateQuestionFromText(String content, String markType);

}
