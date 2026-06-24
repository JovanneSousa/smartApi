package com.jovanne.smartApi.domain.exceptions.apiExceptions;

import com.jovanne.smartApi.infraestructure.http.response.external.ApiErrorResponse;

import java.util.List;

public class ApiServerErrorException extends ApiClientException {
    public ApiServerErrorException(ApiErrorResponse errorResponse) {
        super(500, "Erro interno no serviço externo", errorResponse);
    }

    public ApiServerErrorException(String message) {
        var errorResponse = new ApiErrorResponse(
                false,
                List.of()
        );
        super(400, message, errorResponse);
    }
}
