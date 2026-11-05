package com.dexxf.iris.auth.application.result;

import java.time.Instant;
import java.util.UUID;

public record RefreshTokenRotationResult(
        String tokenId,
        UUID authId,
        String plainRefreshToken,
        Instant expiresAt
) {}