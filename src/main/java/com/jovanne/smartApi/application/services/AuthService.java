package com.jovanne.smartApi.application.services;

import com.jovanne.smartApi.application.dtos.LoginDTO;
import com.jovanne.smartApi.application.dtos.UserDataDTO;
import com.jovanne.smartApi.domain.entities.RennovationResult;
import com.jovanne.smartApi.domain.exceptions.apiExceptions.ApiBadRequestException;
import com.jovanne.smartApi.domain.exceptions.apiExceptions.ApiServerErrorException;
import com.jovanne.smartApi.domain.interfaces.IAuthService;
import com.jovanne.smartApi.infraestructure.http.request.RefreshTokenRequest;
import com.jovanne.smartApi.infraestructure.http.response.external.ExternalApiResponse;
import com.jovanne.smartApi.infraestructure.http.response.external.LoginResponse;
import com.jovanne.smartApi.infraestructure.data.TokenStore;
import com.jovanne.smartApi.infraestructure.http.clients.IAuthClient;
import com.jovanne.smartApi.infraestructure.http.request.LoginRequest;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class AuthService implements IAuthService {
    private final IAuthClient authClient;
    private final TokenStore tokenStore;
    public AuthService(IAuthClient authClient, TokenStore tokenStore) {
        this.authClient = authClient;
        this.tokenStore = tokenStore;
    }

    @Override
    @Tool(name = "execute-login", description = "Realiza login na aplicação de autenticação e retorna o token")
    public void executeLogin(LoginDTO requestDto) {
        var response = authClient.executeLogin(LoginRequest.fromDTO(requestDto));
        var userData = extractUserDataFromResponse(response);
        saveTokens(requestDto.chatId(), userData.jwt(), userData.refreshToken());
    }

    public RennovationResult refreshToken(Long chatId) {
        String refreshToken = tokenStore.getRefreshTokenByChatId(chatId);
        if(refreshToken == null) return RennovationResult.LOGIN_REQUIRED;

        var request = new RefreshTokenRequest(
                UUID.fromString(refreshToken),
                "fin"
        );
        var response = authClient.refreshToken(request);
        if (response == null || !response.isValid())
            return RennovationResult.LOGIN_REQUIRED;
        tokenStore.saveToken(chatId, response.data().token());
        return RennovationResult.SUCCESS;
    }

    private void saveTokens(Long chatId, String token, String refreshToken) {
        tokenStore.saveToken(chatId, token);
        tokenStore.saveRefreshToken(chatId, refreshToken);
    }

    private UserDataDTO extractUserDataFromResponse(ExternalApiResponse<LoginResponse> response) {
        if (!response.isValid())
            throw new ApiServerErrorException("Falha ao extrair dados de resposta do serviço externo");

        return new UserDataDTO(
                response.data().token().accessToken(),
                response.data().token().refreshToken(),
                response.data().token().userToken().name()
        );
    }
}
