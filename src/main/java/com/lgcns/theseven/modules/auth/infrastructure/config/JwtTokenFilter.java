package com.lgcns.theseven.modules.auth.infrastructure.config;

import com.lgcns.theseven.common.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenProvider tokenProvider;
    private final StringRedisTemplate redisTemplate;

    public JwtTokenFilter(JwtTokenProvider tokenProvider, StringRedisTemplate redisTemplate) {
        this.tokenProvider = tokenProvider;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = extractAccessToken(request);
        Claims claims = null;
        boolean expired = false;
        if (token != null) {
            try {
                claims = tokenProvider.parseToken(token);
            } catch (ExpiredJwtException e) {
                claims = e.getClaims();
                expired = true;
            } catch (Exception e) {
                logger.warn("JWT Authentication failed: " + e.getMessage());
            }
        }

        if (claims != null && expired) {
            String refresh = extractRefreshToken(request);
            if (refresh != null) {
                String key = "refreshToken:" + refresh;
                String userId = redisTemplate.opsForValue().get(key);
                if (userId != null && userId.equals(claims.getSubject())) {
                    redisTemplate.delete(key);
                    String newAccess = tokenProvider.generateAccessToken(
                            claims.getSubject(),
                            Map.of("roles", claims.get("roles", List.class))
                    );
                    String newRefresh = tokenProvider.generateRefreshToken(claims.getSubject());
                    redisTemplate.opsForValue().set(
                            "refreshToken:" + newRefresh,
                            claims.getSubject(),
                            Duration.ofSeconds(tokenProvider.getRefreshTokenValidity()));

                    ResponseCookie accessCookie = ResponseCookie.from("accessToken", newAccess)
                            .httpOnly(true)
                            .path("/")
                            .maxAge(Duration.ofSeconds(tokenProvider.getAccessTokenValidity()))
                            .build();
                    ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefresh)
                            .httpOnly(true)
                            .path("/")
                            .maxAge(Duration.ofSeconds(tokenProvider.getRefreshTokenValidity()))
                            .build();
                    response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
                    response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

                    token = newAccess;
                    claims = tokenProvider.parseToken(newAccess);
                    expired = false;
                } else {
                    claims = null;
                }
            } else {
                claims = null;
            }
        }

        if (claims != null && !expired) {
            UserDetails userDetails = User.withUsername(claims.getSubject())
                    .password("")
                    .authorities(((List<String>) claims.get("roles", List.class)).toArray(new String[0]))
                    .build();
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String extractAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        } else if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String extractRefreshToken(HttpServletRequest request) {
        String header = request.getHeader("Refresh-Token");
        if (StringUtils.hasText(header)) {
            return header;
        }
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
