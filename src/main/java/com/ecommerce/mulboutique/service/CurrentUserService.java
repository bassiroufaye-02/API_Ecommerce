package com.ecommerce.mulboutique.service;

import com.ecommerce.mulboutique.entity.User;
import com.ecommerce.mulboutique.exception.NotFoundException;
import com.ecommerce.mulboutique.exception.UnauthorizedException;
import com.ecommerce.mulboutique.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    @Autowired
    private UserRepository userRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new UnauthorizedException("Utilisateur non authentifie");
        }
        String username = authentication.getName();
        return userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouve"));
    }
}
