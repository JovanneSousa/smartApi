package com.jovanne.smartApi.domain.interfaces;

import com.jovanne.smartApi.infraestructure.persistense.entities.TelegramUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository {
    void save(TelegramUser user);
    Optional<TelegramUser> findByChatId(Long chatId);
    boolean existsByChatId(Long chatId);
}
