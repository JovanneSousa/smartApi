package com.jovanne.smartApi.infraestructure.http.response.external;


public record ExternalApiResponse<T extends BaseResponse>(boolean success, T data) {
    public boolean isValid() {
        return data != null && data.isValid();
    }
}
