package com.jovanne.smartApi.domain.interfaces;


import com.jovanne.smartApi.application.dtos.LoginDTO;
import com.jovanne.smartApi.domain.entities.RennovationResult;

public interface IAuthService {
    void executeLogin(LoginDTO login);

    RennovationResult refreshToken(Long chatId);
}
