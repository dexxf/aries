package com.dexxf.iris.auth.adapter.out.persistence.mapper;

import com.dexxf.iris.auth.domain.entity.OAuthData;
import com.dexxf.iris.auth.infrastructure.persistence.entity.OAuthDataEntity;

public final class OAuthDataMapper {

    private OAuthDataMapper() {
    }

    public static OAuthData toDomain(OAuthDataEntity entity) {
        if (entity == null) return null;

        return OAuthData.reconstitute(
                entity.getId(),
                entity.getOauthProvider(),
                entity.getProviderId()
        );
    }

    public static OAuthDataEntity toEntity(OAuthData oauthData) {
        if (oauthData == null) return null;

        OAuthDataEntity entity = new OAuthDataEntity();
        entity.setId(oauthData.getId());
        entity.setOauthProvider(oauthData.getOauthProvider());
        entity.setProviderId(oauthData.getProviderId());
        return entity;
    }
}
