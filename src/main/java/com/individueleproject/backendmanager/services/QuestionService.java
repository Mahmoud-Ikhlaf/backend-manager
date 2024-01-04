package com.individueleproject.backendmanager.services;

import com.individueleproject.backendmanager.entity.Question;
import com.individueleproject.backendmanager.entity.Quiz;
import com.individueleproject.backendmanager.entity.User;
import com.individueleproject.backendmanager.exceptions.ErrorMessage;
import com.individueleproject.backendmanager.models.MessageResponse;
import com.individueleproject.backendmanager.models.QuestionRequest;
import com.individueleproject.backendmanager.models.QuizRequest;
import com.individueleproject.backendmanager.repository.QuestionRepository;
import com.individueleproject.backendmanager.repository.QuizRepository;
import com.individueleproject.backendmanager.repository.UserRepository;
import com.individueleproject.backendmanager.services.interfaces.IQuestionService;
import lombok.RequiredArgsConstructor;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService implements IQuestionService {
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    public boolean checkQuestionExists(String question) {
        Question questionObj = Question.builder().question(question).build();
        Example<Question> example = Example.of(questionObj);
        return questionRepository.exists(example);
    }
    public ResponseEntity<?> saveQuestion(QuestionRequest request, String username) throws IllegalArgumentException, OptimisticEntityLockException {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(Exception::new);

            if(checkQuestionExists(request.getQuestion())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(MessageResponse.builder().message("Vraag bestaat al!").build());
            }

            Optional<Quiz> quiz = quizRepository.findById(request.getQuizId());
            Question question = Question.builder()
                    .question(request.getQuestion())
                    .answer1(request.getAnswer1())
                    .answer2(request.getAnswer2())
                    .answer3(request.getAnswer3())
                    .answer4(request.getAnswer4())
                    .correctAnswer(request.getCorrectAnswer())
                    .quiz(quiz.get())
                    .build();

            quiz.get().getQuestions().add(question);
            questionRepository.save(question);

            return ResponseEntity.ok()
                    .body(question);
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .body(MessageResponse.builder()
                            .message("Er is iets verkeerds gegaan! Probeer het opnieuw!")
                            .build()
                    );
        }
    }

    public ResponseEntity<?> getAllById(Long id, String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(Exception::new);
            Optional<Quiz> quiz = quizRepository.findById(id);

            if (!quiz.isPresent()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ErrorMessage(HttpStatus.NO_CONTENT.value(), new Date(), "NO CONTENT", "Quiz bestaat niet!"));
            }

            List<Question> questions = questionRepository.findByQuizId(id);
            return ResponseEntity.ok().body(questions);
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
            Optional<Question> question = questionRepository.findById(id);
            if (!user.getId().equals(question.get().getQuiz().getUser().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorMessage(HttpStatus.FORBIDDEN.value(), new Date(), "Forbidden", "You don´t have the right permissions!"));
            }
            questionRepository.deleteById(id);
            return ResponseEntity.ok().body(MessageResponse.builder().message("Vraag is succesvol verwijderd!").build());
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .body(MessageResponse.builder()
                            .message("Er is iets verkeerds gegaan! Probeer het opnieuw!")
                            .build()
                    );
        }
    }

    public ResponseEntity<?> updateQuestion(Long id, QuestionRequest request, String username) {
        try {
            Question question = questionRepository.findById(id)
                    .orElseThrow(Exception::new);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(Exception::new);

            if (!user.getId().equals(question.getQuiz().getUser().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorMessage(HttpStatus.FORBIDDEN.value(), new Date(), "Forbidden", "You don´t have the right permissions!"));
            }

            Example<Question> check = Example.of(Question.builder()
                    .question(request.getQuestion())
                    .build());
            boolean exist = questionRepository.exists(check);

            if (exist) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(MessageResponse.builder().message("Vraag bestaat al!").build());
            }

            question.setQuestion(request.getQuestion());
            question.setAnswer1(request.getAnswer1());
            question.setAnswer2(request.getAnswer2());
            question.setAnswer3(request.getAnswer3());
            question.setAnswer4(request.getAnswer4());
            question.setCorrectAnswer(request.getCorrectAnswer());
            questionRepository.save(question);

            return ResponseEntity.ok().body(MessageResponse.builder().message("Vraag is succesvol bijgewerkt!").build());
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .body(MessageResponse.builder()
                            .message("Er is iets verkeerds gegaan! Probeer het opnieuw!")
                            .build()
                    );
        }
    }
}
