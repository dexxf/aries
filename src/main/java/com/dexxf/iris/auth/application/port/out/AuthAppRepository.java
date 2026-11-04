package com.dexxf.iris.auth.application.port.out;

import com.dexxf.iris.auth.domain.entity.Auth;
import com.dexxf.iris.auth.domain.valueobject.OAuthProvider;

import java.util.Optional;
import java.util.UUID;

public interface AuthAppRepository {
    Optional<Auth> findById(UUID id);
    Optional<Auth> findByOAuthProviderId(OAuthProvider provider, String providerId);
    Auth save(Auth auth);
}
