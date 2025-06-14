package com.lgcns.theseven.modules.auth.infrastructure.mapper;

import com.lgcns.theseven.modules.auth.domain.model.Role;
import com.lgcns.theseven.modules.auth.infrastructure.persistence.entity.RoleEntity;

public interface RoleMapper {
    Role toDomain(RoleEntity entity);
    RoleEntity toEntity(Role model);
}