package com.individueleproject.backendmanager.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String question;

    @Column(nullable = false, length = 20)
    private String answer1;

    @Column(nullable = false, length = 20)
    private String answer2;

    @Column(nullable = false, length = 20)
    private String answer3;

    @Column(nullable = false, length = 20)
    private String answer4;

    @Column(nullable = false)
    private int correctAnswer;

    @ManyToOne
    @JoinColumn(name = "quizid", nullable = false)
    @JsonIgnore
    private Quiz quiz;
}
