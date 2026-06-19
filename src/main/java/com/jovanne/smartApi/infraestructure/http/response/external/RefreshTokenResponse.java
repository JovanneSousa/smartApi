package com.jovanne.smartApi.infraestructure.http.response.external;

public record RefreshTokenResponse(String token) implements BaseResponse {
    @Override
    public boolean isValid() {
        return token != null;
    }
}
