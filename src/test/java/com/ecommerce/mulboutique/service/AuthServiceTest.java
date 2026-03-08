package com.ecommerce.mulboutique.service;

import com.ecommerce.mulboutique.dto.auth.SignUpRequest;
import com.ecommerce.mulboutique.entity.User;
import com.ecommerce.mulboutique.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void createUser_setsFieldsAndEncodesPassword() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("john");
        request.setEmail("john@example.com");
        request.setPassword("secret");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPhone("770000000");
        request.setRole(User.Role.CLIENT);

        when(passwordEncoder.encode("secret")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User saved = authService.createUser(request);

        assertNotNull(saved);
        assertEquals("john", saved.getUsername());
        assertEquals("john@example.com", saved.getEmail());
        assertEquals("hashed", saved.getPassword());
        assertEquals("John", saved.getFirstName());
        assertEquals("Doe", saved.getLastName());
        assertEquals("770000000", saved.getPhone());
        assertEquals(User.Role.CLIENT, saved.getRole());
        assertTrue(saved.getEnabled());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("hashed", captor.getValue().getPassword());
    }

    @Test
    void existsChecksDelegateToRepository() {
        when(userRepository.existsByUsername("john")).thenReturn(true);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertTrue(authService.existsByUsername("john"));
        assertTrue(authService.existsByEmail("john@example.com"));
    }
}
