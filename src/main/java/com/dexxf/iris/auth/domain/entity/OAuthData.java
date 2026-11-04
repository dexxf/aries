package com.dexxf.iris.auth.domain.entity;

import com.dexxf.iris.auth.domain.valueobject.OAuthProvider;

import java.util.Objects;
import java.util.UUID;

public final class OAuthData {

    private final UUID id;
    private final OAuthProvider oauthProvider;
    private final String providerId;

    private OAuthData(UUID id, OAuthProvider oauthProvider, String providerId) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.oauthProvider = Objects.requireNonNull(oauthProvider, "oauthProvider must not be null");
        this.providerId = Objects.requireNonNull(providerId, "providerId must not be null");
    }

    public static OAuthData createNew(OAuthProvider oauthProvider, String providerId) {
        return new OAuthData(UUID.randomUUID(), oauthProvider, providerId);
    }

    public static OAuthData reconstitute(UUID id, OAuthProvider oauthProvider, String providerId) {
        return new OAuthData(id, oauthProvider, providerId);
    }

    public UUID getId() {
        return id;
    }

    public OAuthProvider getOauthProvider() {
        return oauthProvider;
    }

    public String getProviderId() {
        return providerId;
    }

}
