package com.individueleproject.backendmanager.services;

import com.individueleproject.backendmanager.entity.RefreshToken;
import com.individueleproject.backendmanager.entity.User;
import com.individueleproject.backendmanager.exceptions.RefreshTokenException;
import com.individueleproject.backendmanager.models.RefreshResponse;
import com.individueleproject.backendmanager.repository.RefreshTokenRepository;
import com.individueleproject.backendmanager.security.jwt.JwtIssuer;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;
import org.springframework.http.ResponseCookie;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtIssuer jwtIssuer;

    @Value("${jwt.refreshToken.expiration}")
    private int expiration;

    public String createToken(User user) {
        var refreshToken = refreshTokenRepository.save(RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(ZonedDateTime.now().plusMinutes(expiration))
                .build());
        return refreshToken.getToken();
    }

    public RefreshResponse refreshToken(HttpServletRequest request) {
        String refreshToken = getCookieValueByName(request);
        var tokenOptional = refreshTokenRepository.findRefreshTokenEntityByToken(refreshToken);
        if (tokenOptional.isEmpty()) {
            throw new RefreshTokenException("Refresh token %s not found!".formatted(refreshToken));
        }
        var token = tokenOptional.get();
        if (isTokenExpired(token.getExpiryDate())) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenException("Refresh token %s is expired!".formatted(refreshToken));
        }
        String jwt = jwtIssuer.issue(token.getUser().getId(), token.getUser().getUsername());
        updateToken(token);
        return RefreshResponse.builder()
                .accessToken(jwt)
                .build();
    }

    public ResponseCookie generateCookie(String value) {
        return ResponseCookie.from("refreshToken", value).maxAge(24 * 60 * 60).httpOnly(true).build();
    }

    public String getCookieValueByName(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, "refreshToken");
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }

    public ResponseCookie getCleanJwtRefreshCookie() {
        return ResponseCookie.from("refreshToken", null).httpOnly(true).build();
    }

    private void updateToken(RefreshToken token) {
        token.setExpiryDate(ZonedDateTime.now(ZoneId.systemDefault()).plusMinutes(expiration));
        refreshTokenRepository.save(token);
    }

    private boolean isTokenExpired(ZonedDateTime expirationTime) {
        return expirationTime.isBefore(ZonedDateTime.now(ZoneId.systemDefault()));
    }

    public Optional<RefreshToken> findTokenById(Long id) {
        return refreshTokenRepository.findById(id);
    }

    @Transactional
    public void deleteByUserId(Long id) {
        refreshTokenRepository.deleteByUserId(id);
    }
}
