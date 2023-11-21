package com.individueleproject.backendmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SecretProperties.class)
public class BackendManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendManagerApplication.class, args);
    }

}
