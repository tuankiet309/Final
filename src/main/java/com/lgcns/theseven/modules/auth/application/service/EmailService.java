package com.lgcns.theseven.modules.auth.application.service;

public interface EmailService {
    void sendOtp(String to, String code);
}
