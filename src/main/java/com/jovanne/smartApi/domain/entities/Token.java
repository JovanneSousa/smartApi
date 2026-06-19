package com.jovanne.smartApi.domain.entities;

public record Token(String accessToken, String refreshToken, int expiresIn, UserToken userToken) {
    public boolean isValid() {
        return accessToken != null &&
                refreshToken != null &&
                userToken != null &&
                userToken.isValid();
    }
}
