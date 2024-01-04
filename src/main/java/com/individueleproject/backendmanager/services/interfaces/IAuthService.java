package com.individueleproject.backendmanager.services.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface IAuthService {
    ResponseEntity<?> logout(HttpServletRequest request);
    ResponseEntity<?> login(String username, String password);
}
