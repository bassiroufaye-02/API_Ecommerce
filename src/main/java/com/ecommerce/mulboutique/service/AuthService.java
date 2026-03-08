package com.ecommerce.mulboutique.service;

import com.ecommerce.mulboutique.dto.auth.SignUpRequest;
import com.ecommerce.mulboutique.entity.User;
import com.ecommerce.mulboutique.repository.UserRepository;
import com.ecommerce.mulboutique.util.TextSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(SignUpRequest signUpRequest) {
        User user = new User();
        user.setUsername(TextSanitizer.clean(signUpRequest.getUsername()));
        user.setEmail(TextSanitizer.clean(signUpRequest.getEmail()));
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setFirstName(TextSanitizer.clean(signUpRequest.getFirstName()));
        user.setLastName(TextSanitizer.clean(signUpRequest.getLastName()));
        user.setPhone(TextSanitizer.clean(signUpRequest.getPhone()));
        user.setRole(signUpRequest.getRole());
        user.setEnabled(true);

        return userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
