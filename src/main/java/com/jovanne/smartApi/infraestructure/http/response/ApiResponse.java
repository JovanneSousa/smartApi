package com.jovanne.smartApi.infraestructure.http.response;


public record ApiResponse<T>(boolean success, T data) {
    public boolean isValid() {
        return data != null;
    }
}
