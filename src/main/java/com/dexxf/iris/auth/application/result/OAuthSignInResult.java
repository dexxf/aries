package com.dexxf.iris.auth.application.result;

import java.util.UUID;

public record OAuthSignInResult(UUID authId, String jwt) {
}
