package com.ecommerce.mulboutique.security;

import com.ecommerce.mulboutique.entity.User;
import com.ecommerce.mulboutique.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> 
                    new UsernameNotFoundException("Utilisateur non trouvé avec le nom d'utilisateur ou l'email : " + usernameOrEmail)
                );

        return UserPrincipal.build(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> 
                    new UsernameNotFoundException("Utilisateur non trouvé avec l'ID : " + id)
                );

        return UserPrincipal.build(user);
    }
}
