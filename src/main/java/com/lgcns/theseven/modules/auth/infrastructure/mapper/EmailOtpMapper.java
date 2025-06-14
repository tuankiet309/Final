package com.lgcns.theseven.modules.auth.infrastructure.mapper;

import com.lgcns.theseven.modules.auth.domain.model.EmailOtp;
import com.lgcns.theseven.modules.auth.infrastructure.persistence.entity.EmailOtpEntity;

public interface EmailOtpMapper {
    EmailOtp toDomain(EmailOtpEntity entity);
    EmailOtpEntity toEntity(EmailOtp model);
}
