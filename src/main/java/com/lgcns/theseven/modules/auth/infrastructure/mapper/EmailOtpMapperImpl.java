package com.lgcns.theseven.modules.auth.infrastructure.mapper;

import com.lgcns.theseven.modules.auth.domain.model.EmailOtp;
import com.lgcns.theseven.modules.auth.infrastructure.persistence.entity.EmailOtpEntity;

public class EmailOtpMapperImpl implements EmailOtpMapper {
    @Override
    public EmailOtp toDomain(EmailOtpEntity entity) {
        if (entity == null) return null;
        return new EmailOtp(
                entity.getId(),
                entity.getUserId(),
                entity.getCode(),
                entity.getExpiryDate()
        );
    }

    @Override
    public EmailOtpEntity toEntity(EmailOtp model) {
        if (model == null) return null;
        EmailOtpEntity entity = new EmailOtpEntity();
        entity.setId(model.getId());
        entity.setUserId(model.getUserId());
        entity.setCode(model.getCode());
        entity.setExpiryDate(model.getExpiryDate());
        return entity;
    }
}
