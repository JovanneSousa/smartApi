package com.jovanne.smartApi.infraestructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    OpenAPI CustomOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("SmartApi")
                        .version("1.0.0")
                        .description("Api financeira por voz")
                        .contact(new Contact()
                                .name("Jovane Sousa")
                                .email("jovanemaciel943@gmail.com")
                        )
                );
    }
}
