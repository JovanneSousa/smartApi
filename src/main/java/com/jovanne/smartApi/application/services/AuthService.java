package com.jovanne.smartApi.application.services;

import com.jovanne.smartApi.application.dtos.LoginDTO;
import com.jovanne.smartApi.domain.interfaces.IAuthService;
import com.jovanne.smartApi.domain.interfaces.IUserRepository;
import com.jovanne.smartApi.infraestructure.persistense.entities.TelegramUser;
import com.jovanne.smartApi.infraestructure.redis.TokenStore;
import com.jovanne.smartApi.infraestructure.http.clients.IAuthClient;
import com.jovanne.smartApi.infraestructure.http.request.LoginRequest;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class AuthService implements IAuthService {
    @Autowired
    IAuthClient authClient;

    @Autowired
    TokenStore tokenStore;

    @Autowired
    IUserRepository repository;

    private static final Duration TOKEN_TTL   = Duration.ofHours(8);
    private static final Duration REFRESH_TTL = Duration.ofDays(7);

    @Override
    @Tool(name = "execute-login", description = "Realiza login na aplicação de autenticação e retorna o token")
    public String executeLogin(LoginDTO login) {
        var request = LoginRequest.fromDTO(login);
        String token = null;

        var response = authClient.executeLogin(request);
        if(response != null && response.data() != null && response.success() && response.data().token() != null)
            token = response.data().token().accessToken();

        return token != null ?
                token :
                new String();
    }

    public void executeLogin(Long chatId, String login, String password) {
        var response = authClient.executeLogin(new LoginRequest(login, password, "fin"));
        var token = response
                .data()
                .token()
                .accessToken();
        saveTokens(chatId, token);
        repository.save(new TelegramUser());
    }

    private void saveTokens(Long chatId, String token) {
    }
}
