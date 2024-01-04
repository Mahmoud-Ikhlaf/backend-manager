package com.individueleproject.backendmanager.controllers;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.individueleproject.backendmanager.models.QuizRequest;
import com.individueleproject.backendmanager.security.jwt.JwtAuthenticationFilter;
import com.individueleproject.backendmanager.security.jwt.JwtDecoder;
import com.individueleproject.backendmanager.services.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/quizzes")
@Tag(name = "Quiz", description = "Endpoints for to manage quizzes")
public class QuizController {
    private final QuizService quizService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtDecoder jwtDecoder;

    @Operation(
            summary = "Create a quiz",
            description = "Authenticated users can create a quiz.")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody QuizRequest request, HttpServletRequest httpRequest) {
        String username = getUsernameFromToken(httpRequest);
        return quizService.saveQuiz(request, username);
    }

    @Operation(
            summary = "Update a quiz",
            description = "Update an existing quiz that belongs to the authenticated user.")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody QuizRequest request, HttpServletRequest httpServletRequest) {
        String username = getUsernameFromToken(httpServletRequest);
        return quizService.updateQuiz(id, request, username);
    }

    @Operation(
            summary = "Get all quizzes",
            description = "Get all quizzes when no user id is supplied. When an user id is given, it returns all quizzess that belongs to that user.")
    @GetMapping
    public ResponseEntity<?> getAllById(@RequestParam(value = "userid", required = false) Long userid) {
        if (userid != null) {
            return quizService.getAllById(userid);
        } else {
            return quizService.getAll();
        }
    }

    @Operation(
            summary = "Get a quiz",
            description = "Get a quiz by id.")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        String username = getUsernameFromToken(httpServletRequest);
        return quizService.getById(id, username);
    }

    @Operation(
            summary = "Delete a quiz",
            description = "Delete a quiz by id that belongs to the authenticated user.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id, HttpServletRequest httpRequest) {
        String username = getUsernameFromToken(httpRequest);
        return quizService.deleteById(id, username);
    }

    @Operation(
            summary = "Create a quiz code",
            description = "Create a quiz code when a user starts a quiz.")
    @PostMapping("/code/{id}")
    public ResponseEntity<?> code(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        String username = getUsernameFromToken(httpServletRequest);
        return quizService.getCode(id, username);
    }

    @Operation(
            summary = "Delete a quiz code",
            description = "Delete a quiz code when the user is finished with the quiz.")
    @DeleteMapping("/code/{id}")
    public ResponseEntity<?> deleteCode(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        String username = getUsernameFromToken(httpServletRequest);
        return quizService.deleteCode(id, username);
    }

    private String getUsernameFromToken(HttpServletRequest httpRequest) {
        Optional<String> token = jwtAuthenticationFilter.extractTokenFromRequest(httpRequest);
        DecodedJWT jwt = jwtDecoder.decode(token.get());
        return jwt.getClaim("username").asString();
    }

}
