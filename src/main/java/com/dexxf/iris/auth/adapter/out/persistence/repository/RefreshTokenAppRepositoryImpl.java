package com.dexxf.iris.auth.adapter.out.persistence.repository;

import com.dexxf.iris.auth.adapter.out.persistence.mapper.RefreshTokenMapper;
import com.dexxf.iris.auth.application.port.out.RefreshTokenAppRepository;
import com.dexxf.iris.auth.application.result.RefreshTokenSaveResult;
import com.dexxf.iris.auth.domain.entity.RefreshToken;
import com.dexxf.iris.auth.infrastructure.persistence.entity.AuthEntity;
import com.dexxf.iris.auth.infrastructure.persistence.entity.RefreshTokenEntity;
import com.dexxf.iris.auth.infrastructure.persistence.repository.JpaAuthRepository;
import com.dexxf.iris.auth.infrastructure.persistence.repository.JpaRefreshTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class RefreshTokenAppRepositoryImpl implements RefreshTokenAppRepository {

    private final JpaRefreshTokenRepository jpaRefreshTokenRepository;
    private final JpaAuthRepository jpaAuthRepository;

    @Override
    public RefreshTokenSaveResult save(RefreshToken refreshToken) {
        Optional<AuthEntity> authEntity = jpaAuthRepository.findById(refreshToken.getAuthId());

        if (authEntity.isEmpty()) return RefreshTokenSaveResult.FAILED;

        try {
            jpaRefreshTokenRepository.saveAndFlush(RefreshTokenMapper.toEntity(refreshToken, authEntity.get()));
            return RefreshTokenSaveResult.SAVED;
        } catch (DataIntegrityViolationException exception) {
            if (jpaRefreshTokenRepository.existsByAuth_IdAndDeviceId(refreshToken.getAuthId(), refreshToken.getDeviceId())) {
                return RefreshTokenSaveResult.DUPLICATE_DEVICE;
            }
            return RefreshTokenSaveResult.FAILED;
        } catch (DataAccessException exception) {
            return RefreshTokenSaveResult.FAILED;
        }
    }

    @Override
    public Optional<RefreshToken> findByAuthIdAndDeviceId(UUID authId, String deviceId) {
        return jpaRefreshTokenRepository.findByAuth_IdAndDeviceId(authId,deviceId)
                .map(RefreshTokenMapper::toDomain);
    }

    @Override
    public boolean updateToken(UUID id, String hashedToken, Instant newExpiry, String ipAddress, String userAgent) {
        try {
            Optional<RefreshTokenEntity> authRefreshTokenEntityOpt = jpaRefreshTokenRepository.findById(id);
            if(authRefreshTokenEntityOpt.isEmpty()) return false;
            RefreshTokenEntity authRefreshTokenEntity = authRefreshTokenEntityOpt.get();

            authRefreshTokenEntity.setExpiresAt(newExpiry);
            authRefreshTokenEntity.setHashedToken(hashedToken);
            authRefreshTokenEntity.setRevoked(false);
            authRefreshTokenEntity.setRevokedAt(null);
            authRefreshTokenEntity.setIpAddress(ipAddress);
            authRefreshTokenEntity.setUserAgent(userAgent);
            authRefreshTokenEntity.setUpdatedAt(Instant.now());

            jpaRefreshTokenRepository.save(authRefreshTokenEntity);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Optional<RefreshToken> findByRefreshTokenId(UUID refreshTokenId) {
        return jpaRefreshTokenRepository.findById(refreshTokenId).map(RefreshTokenMapper::toDomain);
    }

    @Override
    public boolean rotateToken(UUID tokenId, String currentHashedToken, String newHashedToken, Instant newExpiresAt, String ipAddress, String userAgent) {
        return jpaRefreshTokenRepository.rotateToken(tokenId, currentHashedToken, newHashedToken, newExpiresAt, ipAddress, userAgent) == 1;
    }
}
