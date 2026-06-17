package com.jovanne.smartApi.application.dtos;

import com.jovanne.smartApi.infraestructure.http.request.LoginRequest;

public record LoginDTO(String userName, String password, String sys) {
}
