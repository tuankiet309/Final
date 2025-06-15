package com.lgcns.theseven.modules.auth.api.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//TestController for testing. Gen by GPT
@RestController
public class TestController {

    // Không cần xác thực
    @GetMapping("/test/public")
    public String publicApi() {
        return "Hello world - PUBLIC";
    }

    // Chỉ cần đăng nhập (không yêu cầu role)
    @GetMapping("/test/authenticated")
    public String authenticatedApi() {
        return "Hello world - AUTHENTICATED";
    }

    // Yêu cầu đăng nhập và role 'ADMIN'
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/test/admin")
    public String adminOnlyApi() {
        return "Hello world - ADMIN ONLY";
    }

    // Yêu cầu đăng nhập và role 'USER'
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/test/user")
    public String userOnlyApi() {
        return "Hello world - USER ONLY";
    }
}