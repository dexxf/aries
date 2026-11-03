package com.dexxf.iris.auth.application.service;

import com.dexxf.iris.auth.application.command.OAuthSignInCommand;
import com.dexxf.iris.auth.application.error.OAuthSignInError;
import com.dexxf.iris.auth.application.port.in.OAuthSignInUseCase;
import com.dexxf.iris.auth.application.port.out.AuthAppRepository;
import com.dexxf.iris.auth.application.port.out.JwtTokenGeneratorPort;
import com.dexxf.iris.auth.application.result.OAuthSignInResult;
import com.dexxf.iris.auth.domain.entity.Auth;
import com.dexxf.iris.auth.domain.entity.OAuthData;
import com.dexxf.iris.auth.domain.valueobject.OAuthProvider;
import com.dexxf.iris.common.result.Result;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public final class OAuthSignInService implements OAuthSignInUseCase {
    private final AuthAppRepository authRepository;
    private final JwtTokenGeneratorPort jwtTokenGenerator;

    public OAuthSignInService(AuthAppRepository authRepository, JwtTokenGeneratorPort jwtTokenGenerator) {
        this.authRepository = authRepository;
        this.jwtTokenGenerator = jwtTokenGenerator;
    }

    @Override
    public Result<OAuthSignInResult, OAuthSignInError> signIn(OAuthSignInCommand command) {
        if (command == null) return Result.fail(OAuthSignInError.COMMAND_NULL);
        OAuthProvider provider;

        try {
            provider = OAuthProvider.fromRegistrationId(command.registrationId());
        } catch (IllegalArgumentException ex) {
            return Result.fail(OAuthSignInError.INVALID_PROVIDER);
        }

        if (command.providerId() == null || command.providerId().isBlank()) {
            return Result.fail(OAuthSignInError.INVALID_PROVIDER_ID);
        }
        if (command.email() == null || command.email().isBlank()) {
            return Result.fail(OAuthSignInError.INVALID_EMAIL);
        }

        Auth auth = authRepository.findByOAuthProviderId(provider, command.providerId())
                .orElseGet(() -> createAuth(provider, command));
        String jwt = jwtTokenGenerator.generateToken(auth.getId().toString());
        return Result.ok(new OAuthSignInResult(auth.getId(), jwt));
    }

    private Auth createAuth(OAuthProvider provider, OAuthSignInCommand command) {
        Auth auth = Auth.createNew(UUID.randomUUID(), command.email());
        auth.linkOAuthProvider(OAuthData.createNew(provider, command.providerId()), Instant.now());
        return authRepository.save(auth);
    }
}
