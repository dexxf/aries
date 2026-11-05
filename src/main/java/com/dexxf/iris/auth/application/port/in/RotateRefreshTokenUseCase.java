package com.dexxf.iris.auth.application.port.in;

import com.dexxf.iris.auth.application.command.RotateRefreshTokenCommand;
import com.dexxf.iris.auth.application.error.RefreshTokenRotationError;
import com.dexxf.iris.auth.application.result.RefreshTokenRotationResult;
import com.dexxf.iris.common.result.Result;

public interface RotateRefreshTokenUseCase {
    Result<RefreshTokenRotationResult, RefreshTokenRotationError> execute(RotateRefreshTokenCommand command);
}