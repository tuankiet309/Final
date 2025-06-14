package com.lgcns.theseven.modules.auth.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class EmailOtp {
    private UUID id;
    private UUID userId;
    private String code;
    private LocalDateTime expiryDate;

    public EmailOtp(UUID id, UUID userId, String code, LocalDateTime expiryDate) {
        this.id = id;
        this.userId = userId;
        this.code = code;
        this.expiryDate = expiryDate;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
}
