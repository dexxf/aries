package com.dexxf.iris.auth.application.error;

public enum RefreshTokenRotationError {
    INVALID_TOKEN,
    TOKEN_NOT_FOUND,
    TOKEN_REVOKED,
    TOKEN_EXPIRED,
    SAVE_FAILED,
    INVALID_DEVICE_ID
}