package com.jovanne.smartApi.infraestructure.http.response;

import com.jovanne.smartApi.domain.entities.Token;

public record LoginResponse (Token token) {
}
