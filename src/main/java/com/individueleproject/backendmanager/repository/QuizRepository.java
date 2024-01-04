package com.individueleproject.backendmanager.repository;

import com.individueleproject.backendmanager.entity.Question;
import com.individueleproject.backendmanager.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
}
