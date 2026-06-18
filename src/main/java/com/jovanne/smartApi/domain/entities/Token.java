package com.jovanne.smartApi.domain.entities;

public record Token(String accessToken, String refreshToken, int expiresIn, UserToken token) {
}
