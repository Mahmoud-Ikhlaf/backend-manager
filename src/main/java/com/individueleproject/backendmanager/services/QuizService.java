package com.individueleproject.backendmanager.services;

import com.individueleproject.backendmanager.entity.Quiz;
import com.individueleproject.backendmanager.entity.QuizCode;
import com.individueleproject.backendmanager.entity.User;
import com.individueleproject.backendmanager.exceptions.ErrorMessage;
import com.individueleproject.backendmanager.models.MessageResponse;
import com.individueleproject.backendmanager.models.QuizRequest;
import com.individueleproject.backendmanager.repository.QuestionRepository;
import com.individueleproject.backendmanager.repository.QuizCodeRepository;
import com.individueleproject.backendmanager.repository.QuizRepository;
import com.individueleproject.backendmanager.repository.UserRepository;
import com.individueleproject.backendmanager.services.interfaces.IQuizService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.*;

@Service
@RequiredArgsConstructor
public class QuizService implements IQuizService {
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final QuizCodeRepository quizCodeRepository;

    public boolean checkTitleExists(String title) {
        Quiz quiz = Quiz.builder().title(title).build();
        Example<Quiz> example = Example.of(quiz);
        return quizRepository.exists(example);
    }

    public ResponseEntity<?> saveQuiz(QuizRequest request, String username) throws IllegalArgumentException, OptimisticEntityLockException {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(Exception::new);

            if(checkTitleExists(request.getTitle())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(MessageResponse.builder().message("Titel bestaat al!").build());
            }

            Quiz quiz = Quiz.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .user(user)
                    .build();

            user.getQuizzes().add(quiz);
            userRepository.save(user);

            return ResponseEntity.ok()
                    .body(MessageResponse.builder()
                            .message(quiz.getId().toString())
                            .build()
                    );
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .body(MessageResponse.builder()
                            .message("Er is iets verkeerds gegaan! Probeer het opnieuw!")
                            .build()
                    );
        }
    }

    public ResponseEntity<?> getAll() {
        try {
            List<Quiz> quizzes = quizRepository.findAll();
            return ResponseEntity.ok().body(quizzes);
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .body(MessageResponse.builder()
                            .message("Er is iets verkeerds gegaan! Probeer het opnieuw!")
                            .build()
                    );
        }
    }

    public ResponseEntity<?> getAllById(Long id) {
        try {
            Example<Quiz> example = Example.of(Quiz.builder().user(User.builder().id(id).build()).build());
            List<Quiz> quiz = quizRepository.findAll(example);
            return ResponseEntity.ok().body(quiz);
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .body(MessageResponse.builder()
                            .message("Er is iets verkeerds gegaan! Probeer het opnieuw!")
                            .build()
                    );
        }
    }

    public ResponseEntity<?> getById(Long id, String username) {
        try {
            Quiz quiz = quizRepository.findById(id)
                    .orElseThrow(Exception::new);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(Exception::new);

            if (!user.getId().equals(quiz.getUser().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorMessage(HttpStatus.FORBIDDEN.value(), new Date(), "Forbidden", "You don´t have the right permissions!"));
            }
            Quiz quizResponse = quizRepository.findById(id)
                    .orElseThrow(Exception::new);
            return ResponseEntity.ok().body(quizResponse);
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .body(MessageResponse.builder()
                            .message("Er is iets verkeerds gegaan! Probeer het opnieuw!")
                            .build()
                    );
        }
    }

    public ResponseEntity<?> deleteById(Long id, String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(Exception::new);
            Quiz quiz = quizRepository.findById(id)
                    .orElseThrow(Exception::new);;
            if (!user.getId().equals(quiz.getUser().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorMessage(HttpStatus.FORBIDDEN.value(), new Date(), "Forbidden", "You don´t have the right permissions!"));
            }
            questionRepository.deleteAll(quiz.getQuestions());
            quizRepository.deleteById(id);
            return ResponseEntity.ok().body(MessageResponse.builder().message("Quiz is succesvol verwijderd!").build());
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .body(MessageResponse.builder()
                            .message("Er is iets verkeerds gegaan! Probeer het opnieuw!")
                            .build()
                    );
        }
    }

    public ResponseEntity<?> updateQuiz(Long id, QuizRequest request, String username) {
        try {
            Quiz quiz = quizRepository.findById(id)
                    .orElseThrow(Exception::new);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(Exception::new);

            if (!user.getId().equals(quiz.getUser().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorMessage(HttpStatus.FORBIDDEN.value(), new Date(), "Forbidden", "You don´t have the right permissions!"));
            }

            Example<Quiz> check = Example.of(Quiz.builder().title(request.getTitle()).build());
            boolean exist = quizRepository.exists(check);

            if (exist && !Objects.equals(quiz.getTitle(), request.getTitle())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(MessageResponse.builder().message("Titel bestaat al!").build());
            }

            quiz.setTitle(request.getTitle());
            quiz.setDescription(request.getDescription());
            quizRepository.save(quiz);

            return ResponseEntity.ok().body(MessageResponse.builder().message("Quiz is succesvol bijgewerkt!").build());
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .body(MessageResponse.builder()
                            .message("Er is iets verkeerds gegaan! Probeer het opnieuw!")
                            .build()
                    );
        }
    }

    public ResponseEntity<?> getCode(Long id, String username) {
        try {
            Quiz quiz = quizRepository.findById(id)
                    .orElseThrow(Exception::new);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(Exception::new);

            if (quizCodeRepository.existsByUserAndQuiz(user, quiz)) {
                QuizCode code = quizCodeRepository.findByUser(user);
                return ResponseEntity.ok(code.getCode());
            }

            Long quizCodeValue = generateNumericUUID();

            QuizCode code = QuizCode.builder()
                    .code(quizCodeValue)
                    .user(user)
                    .quiz(quiz)
                    .build();

            quizCodeRepository.save(code);

            return ResponseEntity.ok(quizCodeValue);
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .body(MessageResponse.builder()
                            .message("Er is iets verkeerds gegaan! Probeer het opnieuw!")
                            .build()
                    );
        }
    }

    public ResponseEntity<?> deleteCode(Long id, String username) {
        try {
            Quiz quiz = quizRepository.findById(id)
                    .orElseThrow(Exception::new);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(Exception::new);

            if (quizCodeRepository.existsByUserAndQuiz(user, quiz)) {
                QuizCode code = quizCodeRepository.findByUser(user);
                quizCodeRepository.delete(code);
                return ResponseEntity.ok().body(MessageResponse.builder().message("Code is succesvol verwijderd!").build());
            }

            return ResponseEntity.badRequest().build();
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .body(MessageResponse.builder()
                            .message("Er is iets verkeerds gegaan! Probeer het opnieuw!")
                            .build()
                    );
        }
    }

    private static Long generateNumericUUID() {
        UUID uuid = UUID.randomUUID();
        long numericUUID =  Math.abs(uuid.getMostSignificantBits());
        return numericUUID % 1000000;
    }
}
