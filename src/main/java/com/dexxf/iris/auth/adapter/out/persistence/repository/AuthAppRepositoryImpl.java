package com.dexxf.iris.auth.adapter.out.persistence.repository;

import com.dexxf.iris.auth.adapter.out.persistence.mapper.AuthMapper;
import com.dexxf.iris.auth.application.port.out.AuthAppRepository;
import com.dexxf.iris.auth.domain.entity.Auth;
import com.dexxf.iris.auth.domain.valueobject.OAuthProvider;
import com.dexxf.iris.auth.infrastructure.persistence.repository.JpaAuthRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AuthAppRepositoryImpl implements AuthAppRepository {

    private JpaAuthRepository jpa;

    public AuthAppRepositoryImpl(JpaAuthRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Auth> findById(UUID id) {
        return jpa.findById(id).map(AuthMapper::toDomain);
    }

    @Override
    public Optional<Auth> findByOAuthProviderId(OAuthProvider provider, String providerId) {
        return jpa.findByOauthDataOauthProviderAndOauthDataProviderId(provider, providerId).map(AuthMapper::toDomain);
    }

    @Override
    public Auth save(Auth auth) {
        return AuthMapper.toDomain(jpa.save(AuthMapper.toEntity(auth)));
    }

}
