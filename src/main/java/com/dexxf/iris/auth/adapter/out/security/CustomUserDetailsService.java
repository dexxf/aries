package com.dexxf.iris.auth.adapter.out.security;

import com.dexxf.iris.auth.application.port.out.AuthAppRepository;
import com.dexxf.iris.auth.domain.entity.Auth;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthAppRepository authAppRepository;

    public CustomUserDetailsService (AuthAppRepository authAppRepository) {
        this.authAppRepository = authAppRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Auth auth = authAppRepository.findById(UUID.fromString(username))
                .orElseThrow(() -> new UsernameNotFoundException("Username not found. " + username));

        return new AuthPrincipal(auth);
    }
}
