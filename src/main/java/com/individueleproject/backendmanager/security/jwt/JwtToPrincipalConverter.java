package com.individueleproject.backendmanager.security.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.individueleproject.backendmanager.security.services.UserPrincipal;
import org.springframework.stereotype.Component;

@Component
public class JwtToPrincipalConverter {
    public UserPrincipal convert(DecodedJWT jwt) {
        return UserPrincipal.builder()
                .userId(Long.parseLong(jwt.getSubject()))
                .username(jwt.getClaim("username").asString())
                .build();
    }
}
