package com.dexxf.iris.auth.application.command;

import java.util.UUID;

public record IssueRefreshTokenCommand(UUID authId, String deviceId, String userAgent, String ipAddress) {
}
