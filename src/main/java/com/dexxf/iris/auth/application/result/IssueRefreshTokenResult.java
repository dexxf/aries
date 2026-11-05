package com.dexxf.iris.auth.application.result;

import java.time.Instant;
import java.util.UUID;

public record IssueRefreshTokenResult(UUID authId, String rawToken, Instant expiresAt) {
}
