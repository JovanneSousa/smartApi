package com.jovanne.smartApi.infraestructure.config;

import com.jovanne.smartApi.infraestructure.telegram.SmartApiBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Configuration
public class TelegramConfig {

    @Value("${telegram.bot.token}")
    private String token;
    private final SmartApiBot smartApiBot;

    public TelegramConfig(SmartApiBot smartApiBot) {
        this.smartApiBot = smartApiBot;
    }

    @Bean
    public TelegramBotsLongPollingApplication telegramApp() {
        var app = new TelegramBotsLongPollingApplication();
        try {
            app.registerBot(token, smartApiBot);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Erro ao registrar bot do telegram", e);
        }
        return app;
    }


}
