package com.individueleproject.backendmanager.controllers;

import com.individueleproject.backendmanager.entity.User;
import com.individueleproject.backendmanager.models.*;
import com.individueleproject.backendmanager.services.AuthService;
import com.individueleproject.backendmanager.services.RefreshTokenService;
import com.individueleproject.backendmanager.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication/Authorization", description = "Endpoints for Authentication/Authorization")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @Operation(
            summary = "Login with username and password",
            description = "Login with username and password and retrieve a JWT token and a refresh cookie")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return authService.login(request.getUsername(), request.getPassword());
    }

    @Operation(
            summary = "Register with username, email and password",
            description = "Register with username, email and password. This will create the account.")
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
        userService.saveUser(User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(encodedPassword)
                .build());

        return "Gebruiker is succesvol geregistreerd!";
    }

    @Operation(
            summary = "Refresh a JWT token",
            description = "Refresh a JWT token. Send a refresh cookie and if cookie is valid it will return a new JWT token")
    @GetMapping("/refresh")
    public RefreshResponse refresh(HttpServletRequest request) {
        return refreshTokenService.refreshToken(request);
    }

    @Operation(
            summary = "Logout",
            description = "Logout with refresh cookie. It will delete the refresh cookie")
    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        return authService.logout(request);
    }
}
