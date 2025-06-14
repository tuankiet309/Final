package com.lgcns.theseven.modules.auth.application.service;

import com.lgcns.theseven.modules.auth.application.dto.*;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refresh(RefreshRequest request);
    void requestOtp(String email);
    boolean verifyOtp(OtpRequest request);
}
