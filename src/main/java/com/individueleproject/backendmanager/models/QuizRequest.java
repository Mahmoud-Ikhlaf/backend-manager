package com.individueleproject.backendmanager.models;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizRequest {
    private String title;
    private String description;
}
