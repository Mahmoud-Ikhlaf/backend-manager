package com.individueleproject.backendmanager.repository;

import com.individueleproject.backendmanager.entity.Quiz;
import com.individueleproject.backendmanager.entity.QuizCode;
import com.individueleproject.backendmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizCodeRepository extends JpaRepository<QuizCode, Long> {
    boolean existsByUserAndQuiz(User user, Quiz qUiz);
    QuizCode findByUser(User user);
}
