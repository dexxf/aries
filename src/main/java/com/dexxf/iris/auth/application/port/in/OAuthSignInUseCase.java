package com.dexxf.iris.auth.application.port.in;

import com.dexxf.iris.auth.application.command.OAuthSignInCommand;
import com.dexxf.iris.auth.application.error.OAuthSignInError;
import com.dexxf.iris.auth.application.result.OAuthSignInResult;
import com.dexxf.iris.common.result.Result;

public interface OAuthSignInUseCase {
    Result<OAuthSignInResult, OAuthSignInError> signIn(OAuthSignInCommand command);
}
