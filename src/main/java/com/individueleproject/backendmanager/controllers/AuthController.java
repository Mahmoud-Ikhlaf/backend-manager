package com.individueleproject.backendmanager.controllers;

import com.individueleproject.backendmanager.entity.User;
import com.individueleproject.backendmanager.models.*;
import com.individueleproject.backendmanager.services.AuthService;
import com.individueleproject.backendmanager.services.RefreshTokenService;
import com.individueleproject.backendmanager.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return authService.login(request.getUsername(), request.getPassword());
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {

        boolean usernameNotValid = userService.checkUsername(request.getUsername());
        if(usernameNotValid){
            return "Gebruiksnaam bestaat al!";
        }

        boolean emailNotValid = userService.checkEmail(request.getEmail());
        if(emailNotValid){
            return "Email bestaat al!";
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = userService.saveUser(User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(encodedPassword)
                .build());

        return "Gebruiker is succesvol geregistreerd!";
    }

    @GetMapping("/refresh")
    public RefreshResponse refresh(HttpServletRequest request) {
        return refreshTokenService.refreshToken(request);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        return authService.logout(request);
    }
}
