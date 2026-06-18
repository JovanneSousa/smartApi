package com.jovanne.smartApi.application.services;

import com.jovanne.smartApi.application.dtos.LoginDTO;
import com.jovanne.smartApi.domain.entities.Token;
import com.jovanne.smartApi.domain.interfaces.IAuthService;
import com.jovanne.smartApi.infraestructure.http.clients.IAuthClient;
import com.jovanne.smartApi.infraestructure.http.request.LoginRequest;
import com.jovanne.smartApi.infraestructure.http.response.ApiResponse;
import com.jovanne.smartApi.infraestructure.http.response.LoginResponse;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {
    @Autowired
    IAuthClient authClient;

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
}
