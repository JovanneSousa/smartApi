package com.jovanne.smartApi.infraestructure.http.request;

import com.jovanne.smartApi.application.dtos.LoginDTO;

public record LoginRequest(String userName, String password, String sys) {
    public static LoginRequest fromDTO(LoginDTO dto) {
        return new LoginRequest(
                dto.userName(),
                dto.password(),
                dto.sys()
        );
    }
}
