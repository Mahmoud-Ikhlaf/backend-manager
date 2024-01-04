package com.individueleproject.backendmanager.services.interfaces;

import com.individueleproject.backendmanager.models.QuestionRequest;
import org.springframework.http.ResponseEntity;

public interface IQuestionService {
    boolean checkQuestionExists(String question);
    ResponseEntity<?> saveQuestion(QuestionRequest request, String username);
    ResponseEntity<?> getAllById(Long id, String username);
    ResponseEntity<?> deleteById(Long id, String username);
    ResponseEntity<?> updateQuestion(Long id, QuestionRequest request, String username);
}
