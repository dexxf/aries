package com.dexxf.iris.auth.infrastructure.persistence.repository;

import com.dexxf.iris.auth.infrastructure.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface JpaRefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {
    boolean existsByAuth_IdAndDeviceId(UUID authId, String deviceId);
    Optional<RefreshTokenEntity> findByAuth_IdAndDeviceId(UUID authId, String deviceId);

    @Modifying
    @Query("""
    UPDATE RefreshTokenEntity r
    SET r.hashedToken = :newHashedToken,
        r.expiresAt = :newExpiresAt,
        r.ipAddress = :ipAddress,
        r.userAgent = :userAgent
    WHERE r.id = :tokenId
      AND r.hashedToken = :currentHashedToken
      AND r.revoked = false
      AND r.expiresAt > CURRENT_TIMESTAMP
    """)
    int rotateToken(UUID tokenId, String currentHashedToken, String newHashedToken, Instant newExpiresAt, String ipAddress, String userAgent);
}
