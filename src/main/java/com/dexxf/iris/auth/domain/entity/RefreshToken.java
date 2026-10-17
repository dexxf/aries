package com.dexxf.iris.auth.domain.entity;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class RefreshToken {

    private final UUID id;
    private final UUID authId;
    private final String hashedToken;
    private final Instant expiresAt;
    private boolean revoked;
    private Instant revokedAt;
    private Instant lastUsedAt;
    private final String deviceId;
    private String userAgent;
    private String ipAddress;

    private RefreshToken(UUID id, UUID authId, String hashedToken, Instant expiresAt,
                         boolean revoked, Instant revokedAt, Instant lastUsedAt,
                         String deviceId, String userAgent, String ipAddress) {
        this.id = id;
        this.authId = Objects.requireNonNull(authId, "authId must not be null");
        this.hashedToken = Objects.requireNonNull(hashedToken, "hashedToken must not be null");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt must not be null");
        this.revoked = revoked;
        this.revokedAt = revokedAt;
        this.lastUsedAt = lastUsedAt;
        this.deviceId = Objects.requireNonNull(deviceId, "deviceId must not be null");
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
    }

    public static RefreshToken createNew(UUID authId, String hashedToken, Instant expiresAt,
                                         String deviceId, String userAgent, String ipAddress) {
        return new RefreshToken(UUID.randomUUID() ,authId, hashedToken, expiresAt, false, null, null, deviceId, userAgent, ipAddress);
    }

    public static RefreshToken reconstitute(UUID id, UUID authId, String hashedToken, Instant expiresAt,
                                            boolean revoked, Instant revokedAt, Instant lastUsedAt,
                                            String deviceId, String userAgent, String ipAddress) {
        return new RefreshToken(id, authId, hashedToken, expiresAt,
                revoked, revokedAt, lastUsedAt, deviceId, userAgent, ipAddress);
    }

    public void revoke() {
        if (this.revoked) {
            return;
        }
        this.revoked = true;
        this.revokedAt = Instant.now();
    }

    public void markUsed() {
        this.lastUsedAt = Instant.now();
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiresAt);
    }

    public boolean isActive() {
        return !this.revoked && !this.isExpired();
    }

    public UUID getId() {
        return id;
    }

    public UUID getAuthId() {
        return authId;
    }

    public String getHashedToken() {
        return hashedToken;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public Instant getLastUsedAt() {
        return lastUsedAt;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}