package org.example.ai_integration;

import java.util.Map;

public class Question {
    private String question;
    private Map<String, String> options; // A, B, C, D
    private String answer;

    public Question(String question, Map<String, String> options, String answer) {
        this.question = question;
        this.options = options;
        this.answer = answer;
    }

    public String getQuestion() { return question; }
    public Map<String, String> getOptions() { return options; }
    public String getAnswer() { return answer; }
}
