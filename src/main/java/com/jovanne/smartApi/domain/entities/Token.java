package com.jovanne.smartApi.domain.entities;

public record Token(String accessToken, int expiresIn, UserToken token) {
}
