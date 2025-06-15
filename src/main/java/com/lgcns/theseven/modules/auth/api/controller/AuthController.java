package com.lgcns.theseven.modules.auth.api.controller;

import com.lgcns.theseven.modules.auth.application.service.AuthService;
import com.lgcns.theseven.modules.auth.application.dto.*;
import jakarta.servlet.http.HttpServletResponse;
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
        addAccessCookie(response, auth);
        return ResponseEntity.ok(auth);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Validated @RequestBody LoginRequest request) {
        authService.login(request);
        return ResponseEntity.ok("Confirmation email sent");
    }

    @GetMapping("/login/confirm")
    public ResponseEntity<AuthResponse> confirmLogin(@RequestParam String token,
                                                     HttpServletResponse response) {
        AuthResponse auth = authService.confirmLogin(token);
        addAccessCookie(response, auth);
        return ResponseEntity.ok(auth);
    }

    @GetMapping("/confirm-email")
    public ResponseEntity<Void> confirmEmail(@RequestParam String token) {
        authService.confirmEmail(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Validated @RequestBody RefreshRequest request,
                                                HttpServletResponse response) {
        AuthResponse auth = authService.refresh(request);
        addAccessCookie(response, auth);
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

    private void addAccessCookie(HttpServletResponse response, AuthResponse auth) {
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", auth.getAccessToken())
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofHours(1))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
    }
}
