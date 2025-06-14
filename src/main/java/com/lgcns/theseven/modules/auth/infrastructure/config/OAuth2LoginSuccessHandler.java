package com.lgcns.theseven.modules.auth.infrastructure.config;

import com.lgcns.theseven.common.jwt.JwtTokenProvider;
import com.lgcns.theseven.modules.auth.infrastructure.persistence.entity.RefreshTokenEntity;
import com.lgcns.theseven.modules.auth.domain.repository.RefreshTokenRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Generate JWT token on successful OAuth2 login.
 */
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String userId = (String) oauthUser.getAttribute("userId");
        String access = tokenProvider.generateAccessToken(userId, Map.of("roles", List.of("USER")));
        String refresh = tokenProvider.generateRefreshToken(userId);
        saveRefreshToken(UUID.fromString(userId), refresh);

        response.setContentType("application/json");
        response.getWriter().write("{\"accessToken\":\"" + access + "\",\"refreshToken\":\"" + refresh + "\"}");
    }

    private void saveRefreshToken(UUID userId, String token) {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setUserId(userId);
        entity.setToken(token);
        entity.setExpiryDate(LocalDateTime.now().plusDays(1));
        refreshTokenRepository.save(entity);
    }
}
