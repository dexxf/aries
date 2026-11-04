package com.dexxf.iris.auth.domain.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class Auth {

    private final UUID id;
    private String email;
    private final List<OAuthData> oauthData;
    private final Instant createdAt;
    private Instant updatedAt;

    private Auth(UUID id, String email, List<OAuthData> oauthData, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.oauthData = Objects.requireNonNull(oauthData, "oAuthData must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    public static Auth createNew(UUID id,String email) {
        Instant now = Instant.now();
        List<OAuthData> data = new ArrayList<>();

        return new Auth(id, email, data, now, now);
    }

    public static Auth reconstitute(UUID id, String email, List<OAuthData> oAuthData,
                                    Instant createdAt, Instant updatedAt) {
        return new Auth(id, email, new ArrayList<>(oAuthData), createdAt, updatedAt);
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void updateEmail(String newEmail, Instant now) {
        this.email = Objects.requireNonNull(newEmail);
        this.updatedAt = Objects.requireNonNull(now);
    }

    public List<OAuthData> getOauthData() {
        return List.copyOf(oauthData);
    }

    public boolean linkOAuthProvider(OAuthData oAuthData, Instant now) {
        Objects.requireNonNull(now);
        boolean alreadyLinked = this.oauthData.stream()
                .anyMatch(d -> d.getOauthProvider() == oAuthData.getOauthProvider());
        if (alreadyLinked) {
            return false;
        }

        this.oauthData.add(oAuthData);
        this.updatedAt = now;
        return true;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

}