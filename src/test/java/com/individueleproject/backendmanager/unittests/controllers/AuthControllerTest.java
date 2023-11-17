package com.individueleproject.backendmanager.unittests.controllers;

import com.individueleproject.backendmanager.controllers.AuthController;
import com.individueleproject.backendmanager.entity.User;
import com.individueleproject.backendmanager.models.LoginRequest;
import com.individueleproject.backendmanager.models.LoginResponse;
import com.individueleproject.backendmanager.models.RegisterRequest;
import com.individueleproject.backendmanager.services.AuthService;
import com.individueleproject.backendmanager.services.RefreshTokenService;
import com.individueleproject.backendmanager.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks
    AuthController authController;

    @Mock
    UserService userService;

    @Mock
    AuthService authService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    public void LoginUserSuccess() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "test").maxAge(24 * 60 * 60).httpOnly(true).build();

        LoginResponse expectedLogin = LoginResponse.builder().accessToken("token").build();
        ResponseEntity entity = ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(expectedLogin);

        when(authService.login("test", passwordEncoder.encode("test"))).thenReturn(entity);

        LoginRequest req = LoginRequest.builder()
                .username("test")
                .password(passwordEncoder.encode("test"))
                .build();
        ResponseEntity<?> response = authController.login(req);

        assertNotNull(response.getBody());
        assertThat(((LoginResponse)response.getBody()).getAccessToken()).isEqualTo(expectedLogin.getAccessToken());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders()).containsKey(HttpHeaders.SET_COOKIE);
        assertThat(response.getHeaders().get(HttpHeaders.SET_COOKIE)).contains(refreshCookie.toString());
    }

    @Test
    public void LoginUserFailed() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ResponseEntity entity = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        when(authService.login("test", passwordEncoder.encode("test"))).thenReturn(entity);

        LoginRequest req = LoginRequest.builder()
                .username("test")
                .password(passwordEncoder.encode("test"))
                .build();
        ResponseEntity<?> response = authController.login(req);

        assertNull(response.getBody());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void registerUserSuccess() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(userService.saveUser(any(User.class))).thenReturn(any(User.class));

        RegisterRequest req = RegisterRequest.builder()
                .username("test")
                .email("test@test.com")
                .password(passwordEncoder.encode("test"))
                .build();
        String response = authController.register(req);

        assertThat(response).isEqualTo("Gebruiker is succesvol geregistreerd!");
    }

    @Test
    public void registerUserNameAlreadyInUse() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        willReturn(true).given(userService).checkUsername("test");

        RegisterRequest req = RegisterRequest.builder()
                .username("test")
                .email("test@test.com")
                .password(passwordEncoder.encode("test"))
                .build();
        String response = authController.register(req);

        assertThat(response).isEqualTo("Gebruiksnaam bestaat al!");
    }

    @Test
    public void registerUserEmailAlreadyInUse() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        willReturn(true).given(userService).checkEmail("test@test.com");

        RegisterRequest req = RegisterRequest.builder()
                .username("test")
                .email("test@test.com")
                .password(passwordEncoder.encode("test"))
                .build();
        String response = authController.register(req);

        assertThat(response).isEqualTo("Email bestaat al!");
    }

}