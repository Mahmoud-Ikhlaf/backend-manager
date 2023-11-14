package com.individueleproject.backendmanager.services;

import com.individueleproject.backendmanager.entity.User;

import java.util.Optional;

public interface IUserService {
    Optional<User> findByUsername(String username);
    User saveUser(User user);
    boolean checkUsername(String username);
    boolean checkEmail(String email);
}
