package com.dexxf.iris.auth.adapter.out.security;

import com.dexxf.iris.auth.domain.entity.Auth;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AuthPrincipal implements UserDetails {

    private final Auth auth;

    public AuthPrincipal(Auth auth) { this.auth = auth; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return auth.getId().toString();
    }
}
