package com.dexxf.iris.auth.domain.valueobject;

import java.util.Locale;

public enum OAuthProvider {
    GOOGLE,
    FACEBOOK,
    GITHUB;

    public static OAuthProvider fromRegistrationId(String registrationId) {
        if (registrationId == null || registrationId.isBlank()) {
            throw new IllegalArgumentException("OAuth registration ID must not be blank");
        }
        return OAuthProvider.valueOf(registrationId.trim().toUpperCase(Locale.ROOT));
    }
}
