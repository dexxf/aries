package com.dexxf.iris.auth.application.service;

import com.dexxf.iris.auth.application.port.in.RandomTokenGeneratorUseCase;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class RandomTokenGeneratorService implements RandomTokenGeneratorUseCase {

    private final SecureRandom secureRandom;

    public RandomTokenGeneratorService (SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }
    @Override
    public String generate() {
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
