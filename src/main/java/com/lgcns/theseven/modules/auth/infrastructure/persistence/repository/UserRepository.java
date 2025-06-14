package com.lgcns.theseven.modules.auth.infrastructure.persistence.repository;

import com.lgcns.theseven.modules.auth.infrastructure.persistence.entity.UserEntity;
import com.lgcns.theseven.common.base.repository.BaseRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends BaseRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
}
