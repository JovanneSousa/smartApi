package com.jovanne.smartApi.infraestructure.config;

import com.jovanne.smartApi.infraestructure.data.TokenStore;
import com.jovanne.smartApi.infraestructure.telegram.TelegramContext;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FinanceClientConfig {
    @Autowired
    TelegramContext telegramContext;
    @Autowired
    TokenStore tokenStore;
    @Bean
    public RequestInterceptor financeInterceptor() {
        return requestTemplate -> {
            Long chatId = telegramContext.getChatId();
            if (chatId == null) return;

            String token = tokenStore.getToken(chatId);
            if(token != null) {
                requestTemplate.header("Authorization", "Bearer " + token);
            }
        };
    }
}
