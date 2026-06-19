package com.jovanne.smartApi.application.services;

import com.jovanne.smartApi.application.dtos.LoginDTO;
import com.jovanne.smartApi.application.dtos.UserDataDTO;
import com.jovanne.smartApi.domain.entities.RennovationResult;
import com.jovanne.smartApi.domain.exceptions.apiExceptions.ApiBadRequestException;
import com.jovanne.smartApi.domain.interfaces.IAuthService;
import com.jovanne.smartApi.domain.interfaces.IUserRepository;
import com.jovanne.smartApi.infraestructure.http.request.RefreshTokenRequest;
import com.jovanne.smartApi.infraestructure.http.response.external.ExternalApiResponse;
import com.jovanne.smartApi.infraestructure.http.response.external.LoginResponse;
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
    public void executeLogin(LoginDTO requestDto) {
        var response = authClient.executeLogin(LoginRequest.fromDTO(requestDto));

        var userData = extractUserDataFromResponse(response);

        saveTokens(requestDto.chatId(), userData.jwt(), userData.refreshToken());

        var telUser = new TelegramUser();
        telUser.setChatId(requestDto.chatId());
        telUser.setLogin(userData.email());

        repository.save(telUser);
    }

    public RennovationResult refreshToken(Long chatId) {
        String refreshToken = tokenStore.getRefreshTokenByChatId(chatId);
        if( refreshToken == null ) return RennovationResult.LOGIN_REQUIRED;

        var request = new RefreshTokenRequest(
                UUID.fromString(refreshToken),
                "fin"
        );
        var response = authClient.refreshToken(request);
        if (response == null || !response.isValid())
            return RennovationResult.LOGIN_REQUIRED;
        tokenStore.saveToken(chatId, response.data().token(), TOKEN_TTL);
        return RennovationResult.SUCCESS;
    }

    private void saveTokens(Long chatId, String token, String refreshToken) {
        tokenStore.saveToken(chatId, token, TOKEN_TTL);
        tokenStore.saveRefreshToken(chatId, refreshToken, REFRESH_TTL);
    }

    private UserDataDTO extractUserDataFromResponse(ExternalApiResponse<LoginResponse> response) {
        if (!response.isValid())
            throw new ApiBadRequestException("Falha ao extrair dados de resposta do serviço externo");

        return new UserDataDTO(
                response.data().token().accessToken(),
                response.data().token().refreshToken(),
                response.data().token().userToken().name()
        );
    }
}
