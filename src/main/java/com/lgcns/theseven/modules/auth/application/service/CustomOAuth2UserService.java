package com.lgcns.theseven.modules.auth.application.service;

import com.lgcns.theseven.modules.auth.infrastructure.persistence.entity.RoleEntity;
import com.lgcns.theseven.modules.auth.infrastructure.persistence.entity.UserEntity;
import com.lgcns.theseven.modules.auth.domain.repository.RoleRepository;
import com.lgcns.theseven.modules.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Load or create user information from OAuth2 provider.
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oauthUser = delegate.loadUser(userRequest);

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from provider");
        }

        UserEntity user = userRepository.findByEmail(email).orElseGet(() -> {
            UserEntity newUser = new UserEntity();
            newUser.setUsername(name != null ? name : email);
            newUser.setEmail(email);
            newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            RoleEntity role = roleRepository.findByName("USER")
                    .orElseGet(() -> {
                        RoleEntity r = new RoleEntity();
                        r.setName("USER");
                        r.setDescription("Default role");
                        return roleRepository.save(r);
                    });
            newUser.setRoles(Set.of(role));
            return userRepository.save(newUser);
        });

        Map<String, Object> attributes = new HashMap<>(oauthUser.getAttributes());
        attributes.put("userId", user.getId().toString());

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "email");
    }
}
