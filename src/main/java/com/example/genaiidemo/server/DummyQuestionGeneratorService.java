package com.example.genaiidemo.server;

import org.springframework.stereotype.Service;

@Service("dummyserver")
public class DummyQuestionGeneratorService implements QuestionGeneratorService {
    @Override
    public String generateQuestion(String topic, String difficulty) {
        return "Sample " + difficulty + " question about " + topic;
    }

    @Override
    public String generateQuestionFromText(String extractedText, String difficulty) {
        return "Sample " + difficulty + " question generated from PDF content";
    }
}
