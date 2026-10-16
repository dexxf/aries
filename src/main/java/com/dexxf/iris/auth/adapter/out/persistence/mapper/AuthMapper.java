package com.dexxf.iris.auth.adapter.out.persistence.mapper;

import com.dexxf.iris.auth.domain.entity.Auth;
import com.dexxf.iris.auth.infrastructure.persistence.entity.AuthEntity;

public final class AuthMapper {

    private AuthMapper() {
    }

    public static Auth toDomain(AuthEntity entity) {
        if (entity == null) return null;

        return Auth.reconstitute(
                entity.getId(),
                entity.getEmail(),
                entity.getOauthData().stream()
                        .map(OAuthDataMapper::toDomain)
                        .toList(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static AuthEntity toEntity(Auth auth) {
        if (auth == null) return null;

        AuthEntity entity = new AuthEntity();
        entity.setId(auth.getId());
        entity.setEmail(auth.getEmail());
        entity.setCreatedAt(auth.getCreatedAt());
        entity.setUpdatedAt(auth.getUpdatedAt());

        auth.getOauthData().stream()
                .map(OAuthDataMapper::toEntity)
                .forEach(entity::addOAuthData);

        return entity;
    }
}
