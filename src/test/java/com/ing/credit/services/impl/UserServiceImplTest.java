package com.ing.credit.services.impl;

import com.ing.credit.config.JwtUtil;
import com.ing.credit.dao.entities.CustomerEntity;
import com.ing.credit.dao.entities.UserEntity;
import com.ing.credit.dao.repositories.CustomerRepository;
import com.ing.credit.dao.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;
    @Mock
    UserRepository userRepository;
    @Mock
    CustomerRepository customerRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JwtUtil jwtUtil;
    @Captor
    ArgumentCaptor<UserEntity> userCaptor;

    @Test
    void create_admin_user_with_valid_request() {

        var password = "password";

        when(userRepository.existsByUsername("admin")).thenReturn(Boolean.FALSE);
        when(passwordEncoder.encode(password)).thenReturn("pass");

        userService.createAdminUser("admin", password);

        verify(userRepository).save(userCaptor.capture());
        var user = userCaptor.getValue();

        assertEquals("admin", user.getUsername());
        assertEquals("pass", user.getPassword());
        assertEquals("ADMIN", user.getRoles());
    }

    @Test
    void throw_exception_when_there_is_a_user_with_same_username() {
        var username = "admin";
        var password = "password";

        when(userRepository.existsByUsername("admin")).thenReturn(Boolean.TRUE);

        var runtimeException = assertThrows(RuntimeException.class, () -> {
            userService.createAdminUser(username, password);
        });
        assertEquals("Username is already in use", runtimeException.getMessage());
    }

    @Test
    void login_with_valid_request() {
        var username = "admin";
        var password = "password";

        var userId = UUID.randomUUID();
        var customerId = UUID.randomUUID();
        var jwtToken = "abc";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new UserEntity(userId, username, "pass", "ADMIN")));
        when(passwordEncoder.matches(password, "pass")).thenReturn(true);
        when(jwtUtil.generateToken(username, userId, customerId, "ADMIN")).thenReturn(jwtToken);
        when(customerRepository.findByUser_Id(userId)).thenReturn(Optional.of(new CustomerEntity(customerId, null, null, null, null, null)));

        var response = userService.login(username, password);
        assertEquals("abc", response);
    }

    @Test
    void login_with_invalid_credentials() {
        var username = "admin";
        var password = "password";

        var userId = UUID.randomUUID();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new UserEntity(userId, username, "pass", "ADMIN")));
        when(passwordEncoder.matches(password, "pass")).thenReturn(false);

        var runtimeException = assertThrows(RuntimeException.class, () -> {
            userService.login(username, password);
        });
        assertEquals("Wrong password", runtimeException.getMessage());

    }

}