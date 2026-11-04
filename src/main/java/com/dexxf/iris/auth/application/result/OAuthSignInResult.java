package com.dexxf.iris.auth.application.result;

import java.time.Instant;
import java.util.UUID;

public record OAuthSignInResult(UUID authId, String jwt, String refreshToken, Instant refreshTokenExpiresAt) { }