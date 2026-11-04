package com.dexxf.iris.auth.infrastructure.persistence.repository;

import com.dexxf.iris.auth.infrastructure.persistence.entity.AuthEntity;
import com.dexxf.iris.auth.domain.valueobject.OAuthProvider;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaAuthRepository extends JpaRepository<AuthEntity, UUID> {
    @EntityGraph(attributePaths = "oauthData")
    Optional<AuthEntity> findByOauthDataOauthProviderAndOauthDataProviderId(
            OAuthProvider provider,
            String providerId
    );

    @EntityGraph(attributePaths = "oauthData")
    Optional<AuthEntity> findById(UUID id);
}
