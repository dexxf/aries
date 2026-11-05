
package com.dexxf.iris.auth.application.service;

import com.dexxf.iris.auth.application.command.RotateRefreshTokenCommand;
import com.dexxf.iris.auth.application.error.RefreshTokenRotationError;
import com.dexxf.iris.auth.application.port.in.RotateRefreshTokenUseCase;
import com.dexxf.iris.auth.application.port.in.RandomTokenGeneratorUseCase;
import com.dexxf.iris.auth.application.port.out.HashPort;
import com.dexxf.iris.auth.application.port.out.RefreshTokenAppRepository;
import com.dexxf.iris.auth.application.port.out.RefreshTokenLifetimePolicy;
import com.dexxf.iris.auth.application.result.RefreshTokenRotationResult;
import com.dexxf.iris.auth.domain.entity.RefreshToken;
import com.dexxf.iris.common.result.Result;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenRotationService implements RotateRefreshTokenUseCase {

    private final RefreshTokenAppRepository refreshTokenAppRepository;
    private final HashPort hashPort;
    private final RandomTokenGeneratorUseCase randomTokenGenerator;
    private final RefreshTokenLifetimePolicy refreshTokenLifetimePolicy;
    private final Clock clock;

    public RefreshTokenRotationService(RefreshTokenAppRepository refreshTokenAppRepository, HashPort hashPort, RandomTokenGeneratorUseCase randomTokenGenerator, RefreshTokenLifetimePolicy refreshTokenLifetimePolicy, Clock clock) {
        this.refreshTokenAppRepository = refreshTokenAppRepository;
        this.hashPort = hashPort;
        this.randomTokenGenerator = randomTokenGenerator;
        this.refreshTokenLifetimePolicy = refreshTokenLifetimePolicy;
        this.clock = clock;
    }

    @Override
    @Transactional
    public Result<RefreshTokenRotationResult, RefreshTokenRotationError> execute(RotateRefreshTokenCommand command) {

        if (command == null) {
            return Result.fail(RefreshTokenRotationError.INVALID_TOKEN);
        }

        if (command.deviceId() == null || command.deviceId().isBlank()) {
            return Result.fail(RefreshTokenRotationError.INVALID_DEVICE_ID);
        }

        if (command.plainRefreshToken() == null
                || command.plainRefreshToken().isBlank()) {
            return Result.fail(RefreshTokenRotationError.INVALID_TOKEN);
        }

        if (command.ipAddress() == null || command.ipAddress().isBlank()
                || command.userAgent() == null || command.userAgent().isBlank()) {
            return Result.fail(RefreshTokenRotationError.INVALID_TOKEN);
        }

        String[] parts = command.plainRefreshToken().split("\\.", 2);

        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            return Result.fail(RefreshTokenRotationError.INVALID_TOKEN);
        }

        UUID tokenId;

        try {
            tokenId = UUID.fromString(parts[1]);
        } catch (IllegalArgumentException e) {
            return Result.fail(RefreshTokenRotationError.INVALID_TOKEN);
        }

        Optional<RefreshToken> tokenOpt = refreshTokenAppRepository.findByRefreshTokenId(tokenId);

        if (tokenOpt.isEmpty()) {
            return Result.fail(RefreshTokenRotationError.TOKEN_NOT_FOUND);
        }

        RefreshToken existingToken = tokenOpt.get();

        if (existingToken.isRevoked()) {
            return Result.fail(RefreshTokenRotationError.TOKEN_REVOKED);
        }

        Instant now = clock.instant();

        if (!existingToken.getExpiresAt().isAfter(now)) {
            return Result.fail(RefreshTokenRotationError.TOKEN_EXPIRED);
        }

        if (!command.deviceId().equals(existingToken.getDeviceId())) {
            return Result.fail(RefreshTokenRotationError.INVALID_DEVICE_ID);
        }

        if (!hashPort.matches(parts[0], existingToken.getHashedToken())) {
            return Result.fail(RefreshTokenRotationError.INVALID_TOKEN);
        }

        String newRawSecret = randomTokenGenerator.generate();
        String newHashedToken = hashPort.encode(newRawSecret);

        Instant newExpiry = refreshTokenLifetimePolicy.issueExpirationFromNow();

        boolean rotated = refreshTokenAppRepository.rotateToken(
                existingToken.getId(),
                existingToken.getHashedToken(),
                newHashedToken,
                newExpiry,
                command.ipAddress(),
                command.userAgent()
        );

        if (!rotated) {
            return Result.fail(RefreshTokenRotationError.SAVE_FAILED);
        }

        return Result.ok(new RefreshTokenRotationResult(
                existingToken.getId().toString(),
                existingToken.getAuthId(),
                buildPlainRefreshToken(newRawSecret, existingToken.getId()),
                newExpiry
        ));
    }

    private String buildPlainRefreshToken(String rawSecret, UUID tokenId) {
        return rawSecret + "." + tokenId;
    }
}
