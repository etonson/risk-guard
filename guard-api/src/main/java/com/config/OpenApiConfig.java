package com.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger) Configuration
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI riskGuardOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Risk Guard API")
                        .description("系統核心 API 規範，包含認證模組與選單模組。認證機制支援 JWT Header 與 HttpOnly Cookie。")
                        .version("1.3.0"))
                .addSecurityItem(new SecurityRequirement()
                        .addList("bearerAuth")
                        .addList("cookieAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT"))
                        .addSecuritySchemes("cookieAuth",
                                new SecurityScheme()
                                        .name("access_token")
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.COOKIE)
                                        .name("access_token")));
    }
}
