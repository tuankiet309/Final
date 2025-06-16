package com.lgcns.theseven.modules.auth.infrastructure.config;

import com.lgcns.theseven.common.jwt.JwtTokenProvider;
import com.lgcns.theseven.modules.auth.application.dto.AuthResponse;
import com.lgcns.theseven.modules.auth.application.dto.RefreshRequest;
import com.lgcns.theseven.modules.auth.application.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

import java.io.IOException;
import java.util.List;

public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenProvider tokenProvider;
    private final AuthService authService;

    public JwtTokenFilter(JwtTokenProvider tokenProvider, AuthService authService) {
        this.tokenProvider = tokenProvider;
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String token = null;
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            token = header.substring(7);
        } else if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null) {
            try {
                Claims claims = tokenProvider.parseToken(token);
                setAuthentication(claims, request);
            } catch (ExpiredJwtException ex) {
                handleRefresh(request, response, ex);
            } catch (Exception e) {
                logger.warn("JWT Authentication failed: " + e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(Claims claims, HttpServletRequest request) {
        UserDetails userDetails = User.withUsername(claims.getSubject())
                .password("")
                .authorities(((List<String>) claims.get("roles", List.class)).toArray(new String[0]))
                .build();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void handleRefresh(HttpServletRequest request, HttpServletResponse response, ExpiredJwtException ex) {
        String refresh = request.getHeader("Refresh-Token");
        if (refresh == null && request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refresh = cookie.getValue();
                    break;
                }
            }
        }
        if (refresh != null) {
            try {
                RefreshRequest req = new RefreshRequest();
                req.setRefreshToken(refresh);
                AuthResponse auth = authService.refresh(req);
                Claims claims = tokenProvider.parseToken(auth.getAccessToken());
                setAuthentication(claims, request);
                ResponseCookie accessCookie = ResponseCookie.from("accessToken", auth.getAccessToken())
                        .httpOnly(true)
                        .path("/")
                        .maxAge(Duration.ofSeconds(tokenProvider.getAccessTokenValidity()))
                        .build();
                response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
                response.setHeader("Refresh-Token", auth.getRefreshToken());
            } catch (Exception refreshEx) {
                logger.warn("JWT refresh failed: " + refreshEx.getMessage());
            }
        } else {
            logger.debug("No refresh token available for expired access token");
        }
    }
}
