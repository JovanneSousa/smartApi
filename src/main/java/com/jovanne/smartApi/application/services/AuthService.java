package com.jovanne.smartApi.application.services;

import com.google.genai.errors.ApiException;
import com.jovanne.smartApi.application.dtos.LoginDTO;
import com.jovanne.smartApi.application.dtos.TokenDTO;
import com.jovanne.smartApi.domain.entities.RennovationResult;
import com.jovanne.smartApi.domain.interfaces.IAuthService;
import com.jovanne.smartApi.domain.interfaces.IUserRepository;
import com.jovanne.smartApi.infraestructure.http.request.RefreshTokenRequest;
import com.jovanne.smartApi.infraestructure.http.response.ApiResponse;
import com.jovanne.smartApi.infraestructure.http.response.LoginResponse;
import com.jovanne.smartApi.infraestructure.persistense.entities.TelegramUser;
import com.jovanne.smartApi.infraestructure.redis.TokenStore;
import com.jovanne.smartApi.infraestructure.http.clients.IAuthClient;
import com.jovanne.smartApi.infraestructure.http.request.LoginRequest;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

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
        var tokens = extractTokensFromResponse(response);
        saveTokens(chatId, tokens.jwt(), tokens.refreshToken());
        repository.save(new TelegramUser());
    }

    public RennovationResult refreshToken(Long chatId) {
        String refreshToken = tokenStore.getRefreshTokenByChatId(chatId);
        if( refreshToken == null) return RennovationResult.LOGIN_REQUIRED;

        var request = new RefreshTokenRequest(
                UUID.fromString(refreshToken),
                "fin"
        );
        try {
            var response = authClient.refreshToken(request);
            if (response == null && !response.isValid())
                return RennovationResult.LOGIN_REQUIRED;
            tokenStore.saveRefreshToken(chatId, response.data(), REFRESH_TTL);
            return RennovationResult.SUCCESS;
        } catch (ApiException ex) {
            return RennovationResult.LOGIN_REQUIRED;
        }
    }

    private void saveTokens(Long chatId, String token, String refreshToken) {
        tokenStore.saveToken(chatId, token, TOKEN_TTL);
        tokenStore.saveRefreshToken(chatId, refreshToken, REFRESH_TTL);
    }

    private TokenDTO extractTokensFromResponse(ApiResponse<LoginResponse> response) {
        if (!response.isValid())
            return null;

        return new TokenDTO(
                response.data().token().accessToken(),
                response.data().token().refreshToken()
        );
    }
}
