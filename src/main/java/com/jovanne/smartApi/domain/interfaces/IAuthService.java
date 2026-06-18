package com.jovanne.smartApi.domain.interfaces;


import com.jovanne.smartApi.application.dtos.LoginDTO;

public interface IAuthService {
    String executeLogin(LoginDTO login);
}
