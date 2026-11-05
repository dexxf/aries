package com.dexxf.iris.auth.application.port.out;

import java.util.UUID;

public interface JwtTokenGeneratorPort {
    String generateToken(UUID authId);
}
