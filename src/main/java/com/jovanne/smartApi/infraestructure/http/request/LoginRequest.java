package com.jovanne.smartApi.infraestructure.http.request;

import com.jovanne.smartApi.application.dtos.LoginDTO;

public record LoginRequest(String email, String password, String system) {
    public static LoginRequest fromDTO(LoginDTO dto) {
        return new LoginRequest(
                dto.email(),
                dto.password(),
                dto.system()
        );
    }
}
