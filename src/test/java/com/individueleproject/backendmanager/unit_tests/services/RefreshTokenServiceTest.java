package com.individueleproject.backendmanager.unit_tests.services;

import com.individueleproject.backendmanager.entity.RefreshToken;
import com.individueleproject.backendmanager.entity.User;
import com.individueleproject.backendmanager.models.RefreshResponse;
import com.individueleproject.backendmanager.repository.RefreshTokenRepository;
import com.individueleproject.backendmanager.security.jwt.JwtIssuer;
import com.individueleproject.backendmanager.services.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {
    @InjectMocks
    RefreshTokenService refreshTokenService;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @Mock
    JwtIssuer jwtIssuer;

    @Mock
    WebUtils webUtils;

    @Test
    public void createToken() {
        User user = User.builder().username("test").password("test").build();
        RefreshToken token = RefreshToken.builder().token("test").build();

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(token);

        String response = refreshTokenService.createToken(user);

        assertNotNull(response);
        assertEquals(response, token.getToken());
    }

    @Test
    public void generateCookieTest() {
        String value = "test";
        ResponseCookie cookie = ResponseCookie.from("refreshToken", value).maxAge(24 * 60 * 60).httpOnly(true).build();

        ResponseCookie response = refreshTokenService.generateCookie(value);

        assertNotNull(response);
        assertTrue(response.isHttpOnly());
        assertEquals(response.getValue(), cookie.getValue());
        assertEquals(response.getName(), cookie.getName());
    }

    @Test
    public void getCookieValueByNameSuccess() {
        Cookie cookie = new Cookie("refreshToken", "value");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(cookie);

        String response = refreshTokenService.getCookieValueByName(request);

        assertNotNull(response);
        assertEquals(response, cookie.getValue());
    }

    @Test
    public void getCookieValueByNameFail() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        String response = refreshTokenService.getCookieValueByName(request);

        assertNull(response);
    }

    @Test
    public void getCleanRefreshToken() {

        ResponseCookie response = refreshTokenService.getCleanJwtRefreshCookie();

        assertEquals(response.getValue(), String.valueOf(""));
        assertTrue(response.isHttpOnly());
    }

    @Test
    public void findTokenByIdSuccess() {
        Long id = 1L;
        Optional<RefreshToken> token = Optional.ofNullable(RefreshToken.builder().token("test").build());
        when(refreshTokenRepository.findById(any(Long.class))).thenReturn(token);

        Optional<RefreshToken> response = refreshTokenService.findTokenById(id);

        assertNotNull(response);
        assertEquals(response.get().getToken(), token.get().getToken());
    }

    @Test
    public void findTokenByIdFail() {
        Long id = 1L;
        when(refreshTokenRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        Optional<RefreshToken> response = refreshTokenService.findTokenById(id);

        assertFalse(response.isPresent());
    }
}
