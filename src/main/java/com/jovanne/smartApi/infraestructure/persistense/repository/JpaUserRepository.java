package com.jovanne.smartApi.infraestructure.persistense.repository;

import com.jovanne.smartApi.domain.interfaces.IUserRepository;
import com.jovanne.smartApi.infraestructure.persistense.entities.TelegramUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaUserRepository implements IUserRepository {
    @Autowired ITelegramUserEntityRepository repository;

    @Override
    public void save(TelegramUser user) {
        repository.save(user);
    }

    @Override
    public Optional<TelegramUser> findByChatId(Long chatId) {
        return repository.findByChatId(chatId);
    }

    @Override
    public boolean existsByChatId(Long chatId) {
        return repository.existsByChatId(chatId);
    }
}
