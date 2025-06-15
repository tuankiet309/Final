package com.lgcns.theseven.modules.auth.application.service;

public interface EmailService {
    void sendOtp(String to, String code);

    /**
     * Send a login confirmation email containing the token link.
     */
    void sendLoginConfirmation(String to, String token);
}
