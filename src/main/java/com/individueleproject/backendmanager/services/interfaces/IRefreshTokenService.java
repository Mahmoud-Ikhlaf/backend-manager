package com.individueleproject.backendmanager.services.interfaces;

import com.individueleproject.backendmanager.entity.RefreshToken;
import com.individueleproject.backendmanager.entity.User;
import com.individueleproject.backendmanager.models.RefreshResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface IRefreshTokenService {
    String createToken(User user);
    RefreshResponse refreshToken(HttpServletRequest request);
    ResponseCookie generateCookie(String value);
    ResponseCookie getCleanJwtRefreshCookie();
    void updateToken(RefreshToken token);
    boolean isTokenExpired(ZonedDateTime expirationTime);
    Optional<RefreshToken> findTokenByUserId(Long id);
    void deleteByUserId(Long id);

}
