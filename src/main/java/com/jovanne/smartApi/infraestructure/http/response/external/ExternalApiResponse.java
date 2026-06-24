package com.jovanne.smartApi.infraestructure.http.response.external;


public record ExternalApiResponse<T>(boolean success, T data) {
    public boolean isValid() {
        return data != null && (!(data instanceof BaseResponse base) || base.isValid());
    }
}
