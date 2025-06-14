package com.lgcns.theseven.modules.auth.infrastructure.mapper;

import com.lgcns.theseven.modules.auth.domain.model.RefreshToken;
import com.lgcns.theseven.modules.auth.infrastructure.persistence.entity.RefreshTokenEntity;

public class RefreshTokenMapperImpl implements RefreshTokenMapper {
    @Override
    public RefreshToken toDomain(RefreshTokenEntity entity) {
        if (entity == null) return null;
        return new RefreshToken(
                entity.getId(),
                entity.getUserId(),
                entity.getToken(),
                entity.getExpiryDate()
        );
    }

    @Override
    public RefreshTokenEntity toEntity(RefreshToken model) {
        if (model == null) return null;
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setId(model.getId());
        entity.setUserId(model.getUserId());
        entity.setToken(model.getToken());
        entity.setExpiryDate(model.getExpiryDate());
        return entity;
    }
}
