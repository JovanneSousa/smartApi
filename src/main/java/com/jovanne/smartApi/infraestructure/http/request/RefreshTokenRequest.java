package com.jovanne.smartApi.infraestructure.http.request;

import java.util.UUID;

public record RefreshTokenRequest(UUID refreshToken, String system) {
}
