package com.lgcns.theseven;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan("com.lgcns.theseven.modules.auth.infrastructure.persistence.entity")

@SpringBootApplication
public class TheSevenApplication {

    public static void main(String[] args) {
        SpringApplication.run(TheSevenApplication.class, args);
    }

}
