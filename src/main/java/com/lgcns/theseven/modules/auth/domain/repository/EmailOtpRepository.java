package com.lgcns.theseven.modules.auth.domain.repository;

import com.lgcns.theseven.modules.auth.infrastructure.persistence.entity.EmailOtpEntity;
import com.lgcns.theseven.common.base.repository.BaseRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmailOtpRepository extends BaseRepository<EmailOtpEntity, UUID> {
    Optional<EmailOtpEntity> findByUserIdAndCode(UUID userId, String code);
}
