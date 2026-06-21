package com.jovanne.smartApi.redis;

import com.jovanne.smartApi.infraestructure.redis.TokenStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TokenStoreIT {

    @Autowired
    TokenStore tokenStore;

//    @Test
//    public void should_save_token() {
//        Long chatId = 123456L;
//        tokenStore.saveToken(chatId, "Token-Teste", Duration.ofMinutes(2));
//        removeToken(chatId);
//    }

    private void removeToken(Long chatId) {
        tokenStore.deleteAll(chatId);
    }

    @Test
    public void should_SaveAndGetToken() {
        Long chatId = 123456L;
        tokenStore.saveToken(chatId, "Token-Teste", Duration.ofMinutes(2));

        var result = tokenStore.getToken(chatId);

        assertNotNull(result);
        assertEquals("Token-Teste", result);

        removeToken(chatId);
    }
}
