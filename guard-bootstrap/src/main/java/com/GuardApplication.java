package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com")
@EnableJpaRepositories(basePackages = "com.infrastructure.repositories")
@EntityScan(basePackages = "com.infrastructure.entities")
public class GuardApplication {
    public static void main(String[] args) {
        SpringApplication.run(GuardApplication.class, args);
    }
}
