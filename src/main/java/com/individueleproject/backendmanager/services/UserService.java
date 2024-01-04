package com.individueleproject.backendmanager.services;

import com.individueleproject.backendmanager.entity.User;
import com.individueleproject.backendmanager.repository.UserRepository;
import com.individueleproject.backendmanager.services.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;

    public boolean checkUsername(String username) {
        User user = User.builder().username(username).build();
        Example<User> example = Example.of(user);
        return userRepository.exists(example);
    }

    public boolean checkEmail(String email) {
        User user = User.builder().email(email).build();
        Example<User> example = Example.of(user);
        return userRepository.exists(example);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
