package com.lgcns.theseven.modules.auth.domain.repository;

import com.lgcns.theseven.modules.auth.infrastructure.persistence.entity.RoleEntity;
import com.lgcns.theseven.common.base.repository.BaseRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends BaseRepository<RoleEntity, UUID> {
    Optional<RoleEntity> findByName(String name);
}
