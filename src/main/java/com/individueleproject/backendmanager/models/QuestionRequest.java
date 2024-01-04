package com.individueleproject.backendmanager.models;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuestionRequest {
    private String question;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private int correctAnswer;
    private Long quizId;
}
