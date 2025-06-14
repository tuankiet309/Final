package com.lgcns.theseven.modules.auth.application.mapper;

import com.lgcns.theseven.modules.auth.domain.model.Role;
import com.lgcns.theseven.modules.auth.infrastructure.persistence.entity.RoleEntity;

public class RoleMapperImpl implements RoleMapper {

    @Override
    public Role toDomain(RoleEntity entity) {
        if (entity == null) return null;

        return new Role(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    @Override
    public RoleEntity toEntity(Role model) {
        if (model == null) return null;

        RoleEntity entity = new RoleEntity();
        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setDescription(model.getDescription());
        entity.setCreatedAt(model.getCreatedAt());
        entity.setUpdatedAt(model.getUpdatedAt());
        return entity;
    }
}