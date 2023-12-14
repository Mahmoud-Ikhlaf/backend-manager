package com.individueleproject.backendmanager.integration_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.individueleproject.backendmanager.BackendManagerApplication;
import com.individueleproject.backendmanager.entity.User;
import com.individueleproject.backendmanager.models.LoginRequest;
import com.individueleproject.backendmanager.models.RegisterRequest;
import com.individueleproject.backendmanager.repository.RefreshTokenRepository;
import com.individueleproject.backendmanager.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = BackendManagerApplication.class)
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties"
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String API_ENDPOINT = "/api/v1/auth";
    private User user;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    public void setup() {
        user = User.builder()
                .id(1L)
                .username("test")
                .password(passwordEncoder.encode("test"))
                .email("test@test.com")
                .build();

        loginRequest = LoginRequest.builder()
                .username("test")
                .password("test")
                .build();

        registerRequest = RegisterRequest.builder()
                .username("test2")
                .email("test2@test.com")
                .password("test2")
                .build();
        userRepository.flush();
        refreshTokenRepository.flush();
        userRepository.save(user);
    }

    @Test
    public void givenLoginCredential_whenLogin_thenStatus200() throws Exception {

        mvc.perform(post(API_ENDPOINT + "/login")
                .content(mapper.writeValueAsString(loginRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsStringIgnoringCase("accessToken")));
    }

    @Test
    public void givenLoginCredential_whenLogin_thenStatus401() throws Exception {

        mvc.perform(post(API_ENDPOINT + "/login")
                        .content(mapper.writeValueAsString(LoginRequest.builder().build()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenRegisterData_whenRegister_thenStatus200() throws Exception {

        mvc.perform(post(API_ENDPOINT + "/register")
                .content(mapper.writeValueAsString(registerRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsStringIgnoringCase("Gebruiker is succesvol geregistreerd!")));
    }

    @Test
    public void givenRegisterData_whenRegister_thenUsernameAlreadyExists() throws Exception {

        mvc.perform(post(API_ENDPOINT + "/register")
                .content(mapper.writeValueAsString(RegisterRequest.builder().username("test").password("test").email("test@test.com").build()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsStringIgnoringCase("Gebruiksnaam bestaat al!")));
    }

    @Test
    public void givenRegisterData_whenRegister_thenEmailAlreadyExists() throws Exception {

        mvc.perform(post(API_ENDPOINT + "/register")
                .content(mapper.writeValueAsString(RegisterRequest.builder().username("a").password("test").email("test@test.com").build()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsStringIgnoringCase("Email bestaat al!")));
    }

}
