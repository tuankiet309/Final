package com.lgcns.theseven.modules.auth.infrastructure.mapper;

import com.lgcns.theseven.modules.auth.domain.model.RefreshToken;
import com.lgcns.theseven.modules.auth.infrastructure.persistence.entity.RefreshTokenEntity;

public interface RefreshTokenMapper {
    RefreshToken toDomain(RefreshTokenEntity entity);
    RefreshTokenEntity toEntity(RefreshToken model);
}
