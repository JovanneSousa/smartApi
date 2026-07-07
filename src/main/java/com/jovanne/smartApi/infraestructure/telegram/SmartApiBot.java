package com.jovanne.smartApi.infraestructure.telegram;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class SmartApiBot implements LongPollingUpdateConsumer {

    private final TelegramContext telegramContext;
    private final TelegramMessageHandler messageHandler;

    public SmartApiBot(TelegramMessageHandler messageHandler, TelegramContext telegramContext) {
        this.telegramContext = telegramContext;
        this.messageHandler = messageHandler;
    }

    @Override
    public void consume(List<Update> updates) {
        updates.forEach(this::processUpdate);
    }

    private void processUpdate(Update update) {
        if(!update.hasMessage()) return;

        var chatId = update.getMessage().getChatId();
        telegramContext.setChatId(chatId);

        try {
            messageHandler.handle(update.getMessage());
        } finally {
            telegramContext.clear();
        }
    }
}
