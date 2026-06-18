package com.jovanne.smartApi.domain.interfaces;

import com.jovanne.smartApi.infraestructure.persistense.entities.TelegramUser;

import java.util.Optional;

public interface IUserRepository {
    void save(TelegramUser user);
    Optional<TelegramUser> findByChatId(Long chatId);
    boolean existsByChatId(Long chatId);
}
