package com.dexxf.iris.auth.application.port.in;

import com.dexxf.iris.auth.application.command.IssueRefreshTokenCommand;
import com.dexxf.iris.auth.application.error.IssueRefreshTokenError;
import com.dexxf.iris.auth.application.result.IssueRefreshTokenResult;
import com.dexxf.iris.common.result.Result;

public interface IssueRefreshTokenUseCase {
    Result<IssueRefreshTokenResult, IssueRefreshTokenError> issue(IssueRefreshTokenCommand command);
}
