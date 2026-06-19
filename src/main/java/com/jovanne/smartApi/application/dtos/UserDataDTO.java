package com.jovanne.smartApi.application.dtos;

public record UserDataDTO(String jwt, String refreshToken, String email) {
    public boolean isValid() {
        return this.jwt != null && this.refreshToken != null && this.email != null;
    }
}
