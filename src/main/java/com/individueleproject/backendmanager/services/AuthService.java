package com.individueleproject.backendmanager.services;

import com.individueleproject.backendmanager.entity.RefreshToken;
import com.individueleproject.backendmanager.entity.User;
import com.individueleproject.backendmanager.models.LoginResponse;
import com.individueleproject.backendmanager.models.MessageResponse;
import com.individueleproject.backendmanager.repository.RefreshTokenRepository;
import com.individueleproject.backendmanager.security.jwt.JwtIssuer;
import com.individueleproject.backendmanager.security.services.UserPrincipal;
import com.individueleproject.backendmanager.services.interfaces.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final JwtIssuer jwtIssuer;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    public ResponseEntity<?> login(String username, String password) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        var principal = (UserPrincipal) authentication.getPrincipal();
        Optional<User> userOptional = userService.findByUsername(principal.getUsername());
        User user = null;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        }
        var token = jwtIssuer.issue(principal.getUserId(), principal.getUsername());
        Optional<RefreshToken> tokenOpt =  refreshTokenService.findTokenByUserId(principal.getUserId());
        String refreshToken = null;
        if (tokenOpt.isEmpty()) {
            refreshToken = refreshTokenService.createToken(user);
        } else {
            if (refreshTokenService.isTokenExpired(tokenOpt.get().getExpiryDate())) {
                refreshTokenRepository.delete(tokenOpt.get());
                refreshToken = refreshTokenService.createToken(user);
            } else {
                refreshToken = tokenOpt.get().getToken();
            }
        }

        ResponseCookie refreshCookie = refreshTokenService.generateCookie(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(LoginResponse.builder().accessToken(token).build());
    }

    public ResponseEntity<?> logout(HttpServletRequest request) {
        String refreshToken = refreshTokenService.getCookieValueByName(request);
        var tokenOptional = refreshTokenRepository.findRefreshTokenEntityByToken(refreshToken);

        if (tokenOptional.isPresent()) {
            Long userId = tokenOptional.get().getUser().getId();
            refreshTokenService.deleteByUserId(userId);
        }

        ResponseCookie refreshCookie = refreshTokenService.getCleanJwtRefreshCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(MessageResponse.builder().message("Gebruiker is succesvol uitgelogd!").build());

    }
}
