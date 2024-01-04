package com.individueleproject.backendmanager.services.interfaces;

import com.individueleproject.backendmanager.models.QuizRequest;
import org.springframework.http.ResponseEntity;

public interface IQuizService {
    boolean checkTitleExists(String title);
    ResponseEntity<?> saveQuiz(QuizRequest request, String username);
    ResponseEntity<?> getAll();
    ResponseEntity<?> getAllById(Long id);
    ResponseEntity<?> getById(Long id, String username);
    ResponseEntity<?> deleteById(Long id, String username);
    ResponseEntity<?> updateQuiz(Long id, QuizRequest request, String username);
    ResponseEntity<?> getCode(Long id, String username);
    ResponseEntity<?> deleteCode(Long id, String username);
}
