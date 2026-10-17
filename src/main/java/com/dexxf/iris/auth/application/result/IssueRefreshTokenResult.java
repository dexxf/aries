package com.dexxf.iris.auth.application.result;

import java.time.Instant;

public record IssueRefreshTokenResult(String authId, String rawToken, Instant expiresAt) {
}
