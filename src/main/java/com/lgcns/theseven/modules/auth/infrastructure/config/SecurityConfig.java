package com.lgcns.theseven.modules.auth.infrastructure.config;

import com.lgcns.theseven.common.jwt.JwtTokenProvider;
import com.lgcns.theseven.modules.auth.application.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider tokenProvider;
    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2LoginSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))                .authorizeHttpRequests(req -> req
                        .requestMatchers("/auth/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .requestMatchers("/auth/test/me").authenticated()
                        .requestMatchers("/auth/test/hello").authenticated()
                        .requestMatchers("/test/public").permitAll()             // Không cần auth
                        .requestMatchers("/test/authenticated", "/test/user", "/test/admin").authenticated() // Cần đăng nhập
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(info -> info.userService(oAuth2UserService))
                        .successHandler(successHandler)
                )
                .addFilterBefore(new JwtTokenFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
