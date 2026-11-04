
package com.dexxf.iris.auth.application.service;

import com.dexxf.iris.auth.application.command.IssueRefreshTokenCommand;
import com.dexxf.iris.auth.application.error.IssueRefreshTokenError;
import com.dexxf.iris.auth.application.port.in.RandomTokenGeneratorUseCase;
import com.dexxf.iris.auth.application.port.in.IssueRefreshTokenUseCase;
import com.dexxf.iris.auth.application.port.out.AuthAppRepository;
import com.dexxf.iris.auth.application.port.out.HashPort;
import com.dexxf.iris.auth.application.port.out.RefreshTokenAppRepository;
import com.dexxf.iris.auth.application.result.RefreshTokenSaveResult;
import com.dexxf.iris.auth.application.result.IssueRefreshTokenResult;
import com.dexxf.iris.auth.domain.entity.RefreshToken;
import com.dexxf.iris.auth.domain.factory.RefreshTokenFactory;
import com.dexxf.iris.common.result.Result;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public final class RefreshTokenService implements IssueRefreshTokenUseCase {

    private final RefreshTokenFactory refreshTokenFactory;
    private final RandomTokenGeneratorUseCase randomTokenGeneratorUseCase;
    private final HashPort hashPort;
    private final AuthAppRepository authAppRepository;
    private final RefreshTokenAppRepository refreshTokenAppRepository;

    public RefreshTokenService(RefreshTokenFactory refreshTokenFactory, RandomTokenGeneratorUseCase randomTokenGeneratorUseCase,
                               @Qualifier("hmacSHA256HashAdapter") HashPort hashPort, AuthAppRepository authAppRepository,
                               RefreshTokenAppRepository refreshTokenAppRepository) {

        this.refreshTokenFactory = refreshTokenFactory;
        this.randomTokenGeneratorUseCase = randomTokenGeneratorUseCase;
        this.hashPort = hashPort;
        this.authAppRepository = authAppRepository;
        this.refreshTokenAppRepository = refreshTokenAppRepository;
    }

    @Override
    public Result<IssueRefreshTokenResult, IssueRefreshTokenError> issue(IssueRefreshTokenCommand command) {

        if (command == null) { return Result.fail(IssueRefreshTokenError.COMMAND_NULL); }

        if (command.deviceId() == null || command.deviceId().isBlank()) { return Result.fail(IssueRefreshTokenError.INVALID_DEVICE_ID);  }

        if (command.userAgent() == null || command.userAgent().isBlank()) { return Result.fail(IssueRefreshTokenError.INVALID_USER_AGENT); }

        if (command.ipAddress() == null || command.ipAddress().isBlank()) { return Result.fail(IssueRefreshTokenError.INVALID_IP_ADDRESS); }

        if (command.authId() == null || authAppRepository.findById(command.authId()).isEmpty()) { return Result.fail(IssueRefreshTokenError.AUTH_ID_NOT_FOUND); }

        String rawToken = randomTokenGeneratorUseCase.generate();
        String hashedToken = hashPort.encode(rawToken);

        RefreshToken newToken = refreshTokenFactory.create( command.authId(), hashedToken, command.deviceId(), command.userAgent(), command.ipAddress() );
        Optional<RefreshToken> existingToken = refreshTokenAppRepository.findByAuthIdAndDeviceId(command.authId(), command.deviceId());

        if (existingToken.isPresent()) {
            RefreshToken token = existingToken.get();

            boolean updated = refreshTokenAppRepository.updateToken(
                    token.getId(), newToken.getHashedToken(),
                    newToken.getExpiresAt(), command.userAgent(), command.ipAddress());

            if (!updated) {
                return Result.fail(IssueRefreshTokenError.SAVE_FAILED);
            }

            return Result.ok(new IssueRefreshTokenResult(command.authId(), rawToken, newToken.getExpiresAt()));
        }

        RefreshTokenSaveResult saveResult = refreshTokenAppRepository.save(newToken);

        if (saveResult == RefreshTokenSaveResult.DUPLICATE_DEVICE) {

            Optional<RefreshToken> racedToken = refreshTokenAppRepository.findByAuthIdAndDeviceId(command.authId(), command.deviceId());

            if (racedToken.isEmpty()) {
                return Result.fail(IssueRefreshTokenError.SAVE_FAILED);
            }

            boolean updated = refreshTokenAppRepository.updateToken(
                    racedToken.get().getId(),
                    hashedToken,
                    newToken.getExpiresAt(),
                    command.userAgent(),
                    command.ipAddress()
            );

            if (!updated) {
                return Result.fail(IssueRefreshTokenError.SAVE_FAILED);
            }

            return Result.ok(new IssueRefreshTokenResult(command.authId(), rawToken, newToken.getExpiresAt()));
        }

        if (saveResult != RefreshTokenSaveResult.SAVED) {
            return Result.fail(IssueRefreshTokenError.SAVE_FAILED);
        }

        return Result.ok(new IssueRefreshTokenResult(command.authId(), rawToken, newToken.getExpiresAt()));
    }
}
