package com.jovanne.smartApi.infraestructure;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class TelegramContext {
    private Long chatId;

    private void setChatId(Long chatId) { this.chatId = chatId; }

    private Long getChatId() { return chatId; }

}
