package com.lgcns.theseven.modules.auth.application.service;

import com.lgcns.theseven.modules.auth.application.dto.*;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticate user credentials and send a confirmation email.
     * Tokens are issued once the confirmation link is accessed.
     */
    void login(LoginRequest request);

    /**
     * Issue JWT tokens after validating the confirmation token.
     */
    AuthResponse confirmLogin(String token);

    /**
     * Verify registration by validating the email confirmation token.
     */
    void confirmEmail(String token);

    AuthResponse refresh(RefreshRequest request);
    void requestOtp(String email);
    boolean verifyOtp(OtpRequest request);
}
