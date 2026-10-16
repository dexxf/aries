package com.dexxf.iris.auth.application.port.out;

import com.dexxf.iris.auth.domain.entity.Auth;

import java.util.Optional;
import java.util.UUID;

public interface AuthAppRepository {
    Optional<Auth> findById(UUID id);
}
