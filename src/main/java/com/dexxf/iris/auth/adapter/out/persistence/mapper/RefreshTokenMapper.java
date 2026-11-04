package com.dexxf.iris.auth.adapter.out.persistence.mapper;


import com.dexxf.iris.auth.domain.entity.RefreshToken;
import com.dexxf.iris.auth.infrastructure.persistence.entity.AuthEntity;
import com.dexxf.iris.auth.infrastructure.persistence.entity.RefreshTokenEntity;

public class RefreshTokenMapper {

    public static RefreshTokenEntity toEntity(RefreshToken refreshToken, AuthEntity authEntity) {
        return RefreshTokenEntity.builder()
                .id(refreshToken.getId())
                .auth(authEntity)
                .hashedToken(refreshToken.getHashedToken())
                .expiresAt(refreshToken.getExpiresAt())
                .revoked(refreshToken.isRevoked())
                .revokedAt(refreshToken.getRevokedAt())
                .lastUsedAt(refreshToken.getLastUsedAt())
                .deviceId(refreshToken.getDeviceId())
                .userAgent(refreshToken.getUserAgent())
                .ipAddress(refreshToken.getIpAddress())
                .build();
    }

    public static RefreshToken toDomain(RefreshTokenEntity entity) {
        return RefreshToken.reconstitute(
                entity.getId(),
                entity.getAuth().getId(),
                entity.getHashedToken(),
                entity.getExpiresAt(),
                entity.isRevoked(),
                entity.getRevokedAt(),
                entity.getLastUsedAt(),
                entity.getDeviceId(),
                entity.getUserAgent(),
                entity.getIpAddress()
        );
    }
}