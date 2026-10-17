package com.dexxf.iris.auth.application.error;

public enum IssueRefreshTokenError {
    INVALID_DEVICE_ID,
    SAVE_FAILED,
    INVALID_USER_AGENT,
    INVALID_IP_ADDRESS,
    ACCOUNT_HAVE_EXISTING_TOKEN,
    COMMAND_NULL, AUTH_ID_NOT_FOUND
}
