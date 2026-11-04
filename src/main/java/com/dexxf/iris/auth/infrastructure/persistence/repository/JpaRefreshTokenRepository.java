package com.dexxf.iris.auth.infrastructure.persistence.repository;

import com.dexxf.iris.auth.infrastructure.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaRefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {
    boolean existsByAuth_IdAndDeviceId(UUID authId, String deviceId);
}
