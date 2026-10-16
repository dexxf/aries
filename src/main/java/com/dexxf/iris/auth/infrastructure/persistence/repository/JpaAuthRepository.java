package com.dexxf.iris.auth.infrastructure.persistence.repository;

import com.dexxf.iris.auth.infrastructure.persistence.entity.AuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaAuthRepository extends JpaRepository<AuthEntity, UUID> {
}
