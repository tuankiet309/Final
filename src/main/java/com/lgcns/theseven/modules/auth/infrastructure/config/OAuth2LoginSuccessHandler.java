package com.lgcns.theseven.modules.auth.infrastructure.config;

import com.lgcns.theseven.common.jwt.JwtTokenProvider;
import org.springframework.data.redis.core.StringRedisTemplate;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
    private final StringRedisTemplate redisTemplate;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private final RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String userId = (String) oauthUser.getAttribute("userId");
        String access = tokenProvider.generateAccessToken(
                userId,
                Map.of("roles", List.of("ROLE_USER"))
        );
        String refresh = tokenProvider.generateRefreshToken(userId);
        saveRefreshToken(UUID.fromString(userId), refresh);

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", access)
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofSeconds(tokenProvider.getAccessTokenValidity()))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader("Refresh-Token", refresh);

        SavedRequest savedRequest = requestCache.getRequest(request, response);
        String targetUrl = savedRequest != null ? savedRequest.getRedirectUrl() : "/test/public";
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    private void saveRefreshToken(UUID userId, String token) {
        String key = "refreshToken:" + token;
        redisTemplate.opsForValue().set(key, userId.toString(), Duration.ofSeconds(tokenProvider.getRefreshTokenValidity()));
    }
}
