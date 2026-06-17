package com.jovanne.smartApi.infraestructure.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FinanceiroClientConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new FinanceiroErroDecoder();
    }
}
