package com.lgcns.theseven.modules.auth.infrastructure.persistence.repository;

import com.lgcns.theseven.modules.auth.infrastructure.persistence.entity.RefreshTokenEntity;
import com.lgcns.theseven.common.base.repository.BaseRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends BaseRepository<RefreshTokenEntity, UUID> {
    Optional<RefreshTokenEntity> findByToken(String token);
    void deleteByUserId(UUID userId);
}
