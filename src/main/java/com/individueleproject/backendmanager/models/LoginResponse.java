package com.individueleproject.backendmanager.models;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
public class LoginResponse {
    private final String accessToken;
}
