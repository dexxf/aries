package com.dexxf.iris.auth.domain.factory;


import com.dexxf.iris.auth.domain.entity.RefreshToken;

import java.util.UUID;

public interface RefreshTokenFactory {
    RefreshToken create(UUID authId, String hashedToken, String deviceId, String userAgent, String ipAddress);
}