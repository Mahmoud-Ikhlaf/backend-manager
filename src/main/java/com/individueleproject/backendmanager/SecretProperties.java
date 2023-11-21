package com.individueleproject.backendmanager;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("property")
public record SecretProperties(String jwtSecret_Key) {
}
