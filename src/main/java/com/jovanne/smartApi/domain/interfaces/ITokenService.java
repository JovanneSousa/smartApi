package com.jovanne.smartApi.domain.interfaces;

import java.time.Duration;

public interface ITokenService {

    void saveToken(Long chatId, String token);

    void saveRefreshToken(Long chatId, String refreshToken);

    String getToken(Long chatId);

    String getRefreshTokenByChatId(Long chatId);

    boolean hasToken(Long chatId);

    void deleteAll(Long chatId);
}
