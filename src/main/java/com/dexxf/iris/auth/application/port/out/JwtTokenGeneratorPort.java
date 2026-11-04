package com.dexxf.iris.auth.application.port.out;

public interface JwtTokenGeneratorPort {
    String generateToken(String authId);
}
