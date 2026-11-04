package com.dexxf.iris.auth.application.port.out;


import com.dexxf.iris.auth.application.result.RefreshTokenSaveResult;
import com.dexxf.iris.auth.domain.entity.RefreshToken;

import java.util.UUID;

public interface RefreshTokenAppRepository {
    RefreshTokenSaveResult save(RefreshToken refreshToken);
    boolean existsByAuthIdAndDeviceId(UUID authId, String deviceId);
}
