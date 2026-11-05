package com.dexxf.iris.auth.application.command;

public record RotateRefreshTokenCommand(
        String plainRefreshToken,
        String deviceId,
        String ipAddress,
        String userAgent
) {
}