package com.lgcns.theseven.modules.auth.application.mapper;



import com.lgcns.theseven.modules.auth.domain.model.User;
import com.lgcns.theseven.modules.auth.infrastructure.persistence.entity.UserEntity;

import java.util.stream.Collectors;

public class UserMapperImpl implements UserMapper {

    private final RoleMapper roleMapper = new RoleMapperImpl();

    @Override
    public User toDomain(UserEntity entity) {
        if (entity == null) return null;

        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRoles() != null
                        ? entity.getRoles().stream()
                        .map(roleMapper::toDomain)
                        .collect(Collectors.toSet())
                        : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    @Override
    public UserEntity toEntity(User model) {
        if (model == null) return null;

        UserEntity entity = new UserEntity();
        entity.setId(model.getId());
        entity.setUsername(model.getUsername());
        entity.setEmail(model.getEmail());
        entity.setPassword(model.getPassword());
        entity.setRoles(model.getRoles() != null
                ? model.getRoles().stream()
                .map(roleMapper::toEntity)
                .collect(Collectors.toSet())
                : null);
        entity.setCreatedAt(model.getCreatedAt());
        entity.setUpdatedAt(model.getUpdatedAt());
        return entity;
    }
}
