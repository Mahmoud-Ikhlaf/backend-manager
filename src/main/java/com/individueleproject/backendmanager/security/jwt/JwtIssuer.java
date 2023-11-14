package com.individueleproject.backendmanager.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class JwtIssuer {
    private final JwtProperties properties;

    @Value("${jwt.token.expiration}")
    private int expTime;

    public String issue(long userId, String username) {
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withExpiresAt(Instant.ofEpochMilli(ZonedDateTime.now(ZoneId.systemDefault()).toInstant().toEpochMilli() + expTime))
                .withClaim("username", username)
                .sign(Algorithm.HMAC256(properties.getSecretKey()));
    }
}
