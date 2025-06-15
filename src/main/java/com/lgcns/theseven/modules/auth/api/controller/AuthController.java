package com.lgcns.theseven.modules.auth.api.controller;

import com.lgcns.theseven.modules.auth.application.service.AuthService;
import com.lgcns.theseven.modules.auth.application.dto.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Validated @RequestBody RegisterRequest request,
                                                 HttpServletResponse response) {
        AuthResponse auth = authService.register(request);
        addCookies(response, auth);
        return ResponseEntity.ok(auth);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Validated @RequestBody LoginRequest request,
                                              HttpServletResponse response) {
        AuthResponse auth = authService.login(request);
        addCookies(response, auth);
        return ResponseEntity.ok(auth);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Validated @RequestBody RefreshRequest request,
                                                HttpServletResponse response,
                                                HttpServletRequest httpRequest) {
        if (request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
            if (httpRequest.getCookies() != null) {
                for (var cookie : httpRequest.getCookies()) {
                    if ("refreshToken".equals(cookie.getName())) {
                        request.setRefreshToken(cookie.getValue());
                        break;
                    }
                }
            }
        }
        AuthResponse auth = authService.refresh(request);
        addCookies(response, auth);
        return ResponseEntity.ok(auth);
    }

    @PostMapping("/otp/request")
    public ResponseEntity<Void> requestOtp(@RequestParam String email) {
        authService.requestOtp(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<Boolean> verifyOtp(@Validated @RequestBody OtpRequest request) {
        return ResponseEntity.ok(authService.verifyOtp(request));
    }

    private void addCookies(HttpServletResponse response, AuthResponse auth) {
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", auth.getAccessToken())
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofHours(1))
                .build();
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", auth.getRefreshToken())
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofDays(1))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
}
