package com.individueleproject.backendmanager.unit_tests.services;

import com.individueleproject.backendmanager.entity.User;
import com.individueleproject.backendmanager.repository.UserRepository;
import com.individueleproject.backendmanager.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Test
    void checkUsernameExistReturnTrue() {
        String username = "test";

        when(userRepository.exists(any(Example.class))).thenReturn(true);

        Boolean response = userService.checkUsername(username);

        assertNotNull(response);
        assertTrue(response);
    }

    @Test
    void checkUsernameExistReturnFalse() {
        String username = "test";

        when(userRepository.exists(any(Example.class))).thenReturn(false);

        Boolean response = userService.checkUsername(username);

        assertNotNull(response);
        assertFalse(response);
    }

    @Test
    void checkEmailExistReturnTrue() {
        String email = "test@test.com";

        when(userRepository.exists(any(Example.class))).thenReturn(true);

        Boolean response = userService.checkUsername(email);

        assertNotNull(response);
        assertTrue(response);
    }

    @Test
    void checkEmailExistReturnFalse() {
        String email = "test@test.com";

        when(userRepository.exists(any(Example.class))).thenReturn(false);

        Boolean response = userService.checkUsername(email);

        assertNotNull(response);
        assertFalse(response);
    }

    @Test
    void saveUserSuccess() {
        User user = User.builder().username("test").password("test").build();

        when(userRepository.save(any(User.class))).thenReturn(user);

        User response = userService.saveUser(user);

        assertNotNull(response);
        assertEquals(response.getUsername(), user.getUsername());
        assertEquals(response.getPassword(), user.getPassword());
    }

    @Test
    void findByUsernameSuccess() {
        String username = "test";
        User user = User.builder().username("test").password("test").build();

        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));

        Optional<User> response = userService.findByUsername(username);

        assertNotNull(response);
        assertEquals(response.get().getUsername(), user.getUsername());
        assertEquals(response.get().getPassword(), user.getPassword());
    }

    @Test
    void findByUsernameFail() {
        String username = "test";
        User user = User.builder().username("test").password("test").build();

        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());

        Optional<User> response = userService.findByUsername(username);

        assertNotNull(response);
        assertInstanceOf(Optional.class, response);
        assertTrue(response.isEmpty());
        assertEquals(Optional.empty(), response);
    }
}
