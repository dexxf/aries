package com.dexxf.iris.auth.application.port.out;

import java.time.Instant;

public interface RefreshTokenLifetimePolicy {
    Instant issueExpirationFromNow();
}