package com.lgcns.theseven.modules.auth.infrastructure.mapper;

import com.lgcns.theseven.modules.auth.domain.model.User;
import com.lgcns.theseven.modules.auth.infrastructure.persistence.entity.UserEntity;

public interface UserMapper {
    User toDomain(UserEntity entity);
    UserEntity toEntity(User model);
}