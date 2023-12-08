package com.individueleproject.backendmanager.unit_tests.services;

import com.individueleproject.backendmanager.entity.RefreshToken;
import com.individueleproject.backendmanager.entity.User;
import com.individueleproject.backendmanager.models.LoginRequest;
import com.individueleproject.backendmanager.models.LoginResponse;
import com.individueleproject.backendmanager.models.MessageResponse;
import com.individueleproject.backendmanager.repository.RefreshTokenRepository;
import com.individueleproject.backendmanager.security.jwt.JwtIssuer;
import com.individueleproject.backendmanager.security.services.UserPrincipal;
import com.individueleproject.backendmanager.services.AuthService;
import com.individueleproject.backendmanager.services.RefreshTokenService;
import com.individueleproject.backendmanager.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    AuthService authService;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    RefreshTokenService refreshTokenService;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @Mock
    UserService userService;

    @Mock
    JwtIssuer jwtIssuer;

    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    void loginUserSuccess() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "test").maxAge(24 * 60 * 60).httpOnly(true).build();
        User user = User.builder()
                .username("test")
                .email("test@test.com")
                .password(passwordEncoder.encode("test"))
                .build();
        RefreshToken token = RefreshToken.builder()
                .token("test")
                .build();
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(UserPrincipal.builder().userId(1L).username("test").password("test").build());
        when(userService.findByUsername(any(String.class))).thenReturn(Optional.of(user));
        when(refreshTokenService.findTokenById(any(Long.class))).thenReturn(Optional.of(token));
        when(refreshTokenService.generateCookie(any(String.class))).thenReturn(refreshCookie);
        when(jwtIssuer.issue(any(Long.class), any(String.class))).thenReturn("test");

        ResponseEntity<?> response = authService.login("test", "test");

        assertNotNull(response.getBody());
        assertThat(((LoginResponse)response.getBody()).getAccessToken()).isEqualTo("test");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders()).containsKey(HttpHeaders.SET_COOKIE);
    }

    @Test
    void logoutSuccess() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "test").maxAge(24 * 60 * 60).httpOnly(true).build();
        Optional<RefreshToken> token = Optional.ofNullable(RefreshToken.builder().token("test").user(User.builder().id(1L).build()).build());

        when(refreshTokenService.getCleanJwtRefreshCookie()).thenReturn(refreshCookie);
        when(refreshTokenRepository.findRefreshTokenEntityByToken(any(String.class))).thenReturn(token);
        when(refreshTokenService.getCookieValueByName(any(HttpServletRequest.class))).thenReturn(String.valueOf(token.get()));

        ResponseEntity<?> response = authService.logout(request);

        assertNotNull(response.getBody());
        assertThat(((MessageResponse)response.getBody()).getMessage()).isEqualTo("Gebruiker is succesvol uitgelogd!");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders()).containsKey(HttpHeaders.SET_COOKIE);
    }
}