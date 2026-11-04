package com.dexxf.iris.auth.application.command;

public record OAuthSignInCommand(
        String registrationId,
        String providerId,
        String email,
        String deviceId,
        String userAgent,
        String ipAddress
) {
}