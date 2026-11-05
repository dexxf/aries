package com.dexxf.iris.auth.adapter.out.factory;

import com.dexxf.iris.auth.domain.entity.RefreshToken;
import com.dexxf.iris.auth.domain.factory.RefreshTokenFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Component
public class RefreshTokenFactoryImpl implements RefreshTokenFactory {

    private final int refreshTokenExpirationByDays;

    public RefreshTokenFactoryImpl(@Value("${security.refresh-token.lifetime-hour}") int refreshTokenExpirationByDays) {
        this.refreshTokenExpirationByDays = refreshTokenExpirationByDays;
    }

    @Override
    public RefreshToken create(UUID authId, String hashedToken, String deviceId, String userAgent, String ipAddress) {
        Instant expiresAt = Instant.now().plus(refreshTokenExpirationByDays, ChronoUnit.DAYS);
        return RefreshToken.createNew(authId, hashedToken, expiresAt, deviceId, userAgent, ipAddress);
    }
}
