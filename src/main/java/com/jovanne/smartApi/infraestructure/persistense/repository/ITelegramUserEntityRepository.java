package com.jovanne.smartApi.infraestructure.persistense.repository;

import com.jovanne.smartApi.infraestructure.persistense.entities.TelegramUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ITelegramUserEntityRepository extends CrudRepository<TelegramUser, String> {
    Optional<TelegramUser> findByChatId(Long chatId);
    boolean existsByChatId(Long chatId);
}
