package com.lgcns.theseven.common.base.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class BaseDomain extends AuditableDomain {

    protected UUID id;

    public BaseDomain(LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String updatedBy, UUID id) {
        super(createdAt, updatedAt, createdBy, updatedBy);
        this.id = id;
    }

    public UUID getId() {
        return id;
    }


}