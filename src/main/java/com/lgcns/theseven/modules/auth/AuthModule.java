package com.lgcns.theseven.modules.auth;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EntityScan("com.lgcns.theseven.modules.auth.infrastructure.persistence.entity")

public class AuthModule {
}
