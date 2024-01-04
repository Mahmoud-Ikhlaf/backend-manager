package com.individueleproject.backendmanager.controllers;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.individueleproject.backendmanager.models.QuestionRequest;
import com.individueleproject.backendmanager.models.QuizRequest;
import com.individueleproject.backendmanager.security.jwt.JwtAuthenticationFilter;
import com.individueleproject.backendmanager.security.jwt.JwtDecoder;
import com.individueleproject.backendmanager.services.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/questions")
@Tag(name = "Question", description = "Endpoints for to manage questions")
public class QuestionController {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtDecoder jwtDecoder;
    private final QuestionService questionService;

    @Operation(
            summary = "Create a question",
            description = "Creates a question that belongs to a quiz.")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody QuestionRequest request, HttpServletRequest httpRequest) {
        String username = getUsernameFromToken(httpRequest);
        return questionService.saveQuestion(request, username);
    }

    @Operation(
            summary = "Get all questions",
            description = "Get all questions that belong to a specific quiz.")
    @GetMapping("/{quizid}")
    public ResponseEntity<?> getAllById(@PathVariable Long quizid, HttpServletRequest httpRequest) {
        String username = getUsernameFromToken(httpRequest);
        return questionService.getAllById(quizid, username);
    }

    @Operation(
            summary = "Delete a question",
            description = "Delete a question that belongs to a specific quiz.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id, HttpServletRequest httpRequest) {
        String username = getUsernameFromToken(httpRequest);
        return questionService.deleteById(id, username);
    }

    @Operation(
            summary = "Update a question",
            description = "Updates a question that belongs to a specific quiz.")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody QuestionRequest request, HttpServletRequest httpServletRequest) {
        String username = getUsernameFromToken(httpServletRequest);
        return questionService.updateQuestion(id, request, username);
    }


    private String getUsernameFromToken(HttpServletRequest httpRequest) {
        Optional<String> token = jwtAuthenticationFilter.extractTokenFromRequest(httpRequest);
        DecodedJWT jwt = jwtDecoder.decode(token.get());
        return jwt.getClaim("username").asString();
    }
}
