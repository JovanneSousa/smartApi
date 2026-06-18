package com.jovanne.smartApi.infraestructure.telegram;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class TelegramContext {
    private Long chatId;

    public void setChatId(Long chatId) { this.chatId = chatId; }

    public Long getChatId() { return chatId; }

}
