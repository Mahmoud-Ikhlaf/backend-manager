package com.individueleproject.backendmanager.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "quizcode")
public class QuizCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long code;

    @ManyToOne
    @JoinColumn(name = "quizid")
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "userid")
    private User user;
}
