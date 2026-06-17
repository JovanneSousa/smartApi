package com.jovanne.smartApi.application.services;

import com.jovanne.smartApi.application.dtos.LoginDTO;
import com.jovanne.smartApi.domain.interfaces.IAuthService;
import com.jovanne.smartApi.infraestructure.http.clients.IAuthClient;
import com.jovanne.smartApi.infraestructure.http.request.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {
    @Autowired
    IAuthClient authClient;
    @Override
    public void executeLogin(LoginDTO login) {
        var request = LoginRequest.fromDTO(login);
        authClient.executeLogin(request);
    }
}
