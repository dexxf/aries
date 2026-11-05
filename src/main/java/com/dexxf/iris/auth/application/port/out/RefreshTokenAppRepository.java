package com.dexxf.iris.auth.application.port.out;


import com.dexxf.iris.auth.application.result.RefreshTokenSaveResult;
import com.dexxf.iris.auth.domain.entity.RefreshToken;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenAppRepository {
    RefreshTokenSaveResult save(RefreshToken refreshToken);
    Optional<RefreshToken> findByAuthIdAndDeviceId(UUID authId, String deviceId);
    boolean updateToken(UUID id, String hashedToken, Instant newExpiry, String ipAddress, String userAgent);
    Optional<RefreshToken> findByRefreshTokenId(UUID refreshTokenID);
    boolean rotateToken(UUID tokenId, String currentHashedToken, String newHashedToken, Instant newExpiresAt, String ipAddress, String userAgent);
}
