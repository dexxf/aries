package com.dexxf.iris.auth.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "auth")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthEntity {

    @Id
    @Column(name = "auth_id")
    private UUID id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @OneToMany(mappedBy = "auth", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OAuthDataEntity> oauthData = new ArrayList<>();

    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public void addOAuthData(OAuthDataEntity oAuthDataEntity) {
        this.oauthData.add(oAuthDataEntity);
        oAuthDataEntity.setAuth(this);
    }

    public void removeOAuthData(OAuthDataEntity oAuthDataEntity) {
        this.oauthData.remove(oAuthDataEntity);
        oAuthDataEntity.setAuth(null);
    }
}
