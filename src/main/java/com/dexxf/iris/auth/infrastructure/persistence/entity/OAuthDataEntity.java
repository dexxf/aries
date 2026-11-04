package com.dexxf.iris.auth.infrastructure.persistence.entity;

import com.dexxf.iris.auth.domain.valueobject.OAuthProvider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(
    name = "oauth_data",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_oauth_provider",
                    columnNames = {"oauth_provider", "provider_id"}
            )
        }
)
public class OAuthDataEntity {

    @Id
    @Column(name = "oauth_data_id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "auth_id", nullable = false)
    private AuthEntity auth;

    @Column(name = "oauth_provider", updatable = false, nullable = false)
    @Enumerated(EnumType.STRING)
    private OAuthProvider oauthProvider;

    @Column(name = "provider_id", nullable = false, updatable = false)
    private String providerId;

    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

}
