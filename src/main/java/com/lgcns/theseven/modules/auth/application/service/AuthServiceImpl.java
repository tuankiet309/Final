package com.lgcns.theseven.modules.auth.application.service;

import com.lgcns.theseven.common.jwt.JwtTokenProvider;
import com.lgcns.theseven.common.unitofwork.UnitOfWork;
import com.lgcns.theseven.modules.auth.application.dto.*;
import com.lgcns.theseven.modules.auth.application.mapper.*;
import com.lgcns.theseven.modules.auth.domain.repository.EmailOtpRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.lgcns.theseven.modules.auth.domain.repository.RoleRepository;
import com.lgcns.theseven.modules.auth.domain.repository.UserRepository;
import com.lgcns.theseven.modules.auth.application.service.EmailService;
import com.lgcns.theseven.modules.auth.infrastructure.persistence.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StringRedisTemplate redisTemplate;
    private final EmailOtpRepository emailOtpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final UnitOfWork unitOfWork;
    private final EmailService emailService;

    private final UserMapper userMapper = new UserMapperImpl();
    private final RoleMapper roleMapper = new RoleMapperImpl();
    private final EmailOtpMapper emailOtpMapper = new EmailOtpMapperImpl();

    @Override
    public AuthResponse register(RegisterRequest request) {
        return unitOfWork.execute(() -> {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new RuntimeException("Email already exists");
            }
            UserEntity entity = new UserEntity();
            entity.setUsername(request.getUsername());
            entity.setEmail(request.getEmail());
            entity.setPassword(passwordEncoder.encode(request.getPassword()));

            RoleEntity role = roleRepository.findByName("USER")
                    .orElseGet(() -> {
                        RoleEntity r = new RoleEntity();
                        r.setName("USER");
                        r.setDescription("Default role");
                        return roleRepository.save(r);
                    });
            entity.setRoles(Set.of(role));
            UserEntity saved = userRepository.save(entity);
            String access = tokenProvider.generateAccessToken(saved.getId().toString(), Map.of("roles", List.of("USER")));
            String refresh = tokenProvider.generateRefreshToken(saved.getId().toString());
            saveRefreshToken(saved.getId(), refresh);
            return new AuthResponse(access, refresh);
        });
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        UserEntity entity = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!passwordEncoder.matches(request.getPassword(), entity.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        String access = tokenProvider.generateAccessToken(entity.getId().toString(), Map.of("roles", extractRoleNames(entity.getRoles())));
        String refresh = tokenProvider.generateRefreshToken(entity.getId().toString());
        saveRefreshToken(entity.getId(), refresh);
        return new AuthResponse(access, refresh);
    }

    @Override
    public AuthResponse refresh(RefreshRequest request) {
        String key = "refreshToken:" + request.getRefreshToken();
        String userId = redisTemplate.opsForValue().get(key);
        if (userId == null) {
            throw new RuntimeException("Invalid refresh token");
        }
        redisTemplate.delete(key);
        String access = tokenProvider.generateAccessToken(userId, Map.of());
        String refresh = tokenProvider.generateRefreshToken(userId);
        saveRefreshToken(UUID.fromString(userId), refresh);
        return new AuthResponse(access, refresh);
    }

    @Override
    public void requestOtp(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));
        String code = String.valueOf(new Random().nextInt(999999));
        EmailOtpEntity otp = new EmailOtpEntity();
        otp.setUserId(user.getId());
        otp.setCode(code);
        otp.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        emailOtpRepository.save(otp);
        emailService.sendOtp(user.getEmail(), code);
    }

    @Override
    public boolean verifyOtp(OtpRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email not found"));
        Optional<EmailOtpEntity> otp = emailOtpRepository.findByUserIdAndCode(user.getId(), request.getCode());
        if (otp.isEmpty()) return false;
        if (otp.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            emailOtpRepository.deleteById(otp.get().getId());
            return false;
        }
        user.setEmailVerified(true);
        userRepository.save(user);
        emailOtpRepository.deleteById(otp.get().getId());
        return true;
    }

    private void saveRefreshToken(UUID userId, String token) {
        String key = "refreshToken:" + token;
        redisTemplate.opsForValue().set(key, userId.toString(), Duration.ofDays(1));
    }

    private List<String> extractRoleNames(Set<RoleEntity> roles) {
        return roles.stream().map(RoleEntity::getName).toList();
    }
}
