package com.jovanne.smartApi.infraestructure.telegram;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.naming.OperationNotSupportedException;

@Component
public class TelegramContext {
    private static final ThreadLocal<Long> CHAT_ID = new ThreadLocal<>();

    public void setChatId(Long chatId) { CHAT_ID.set(chatId); }

    public Long getChatId() { return CHAT_ID.get(); }

    public void clear() {
        CHAT_ID.remove();
    }

}
