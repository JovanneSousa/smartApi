package com.jovanne.smartApi.domain.exceptions.apiExceptions;

import com.jovanne.smartApi.infraestructure.http.response.ApiErrorResponse;

import java.util.List;

public class ApiBadRequestException extends ApiClientException {
    public ApiBadRequestException(ApiErrorResponse errorResponse) {
        super(400, "Dados inválidos enviados ao serviço externo", errorResponse);
    }

    public ApiBadRequestException(String message) {
        var errorResponse = new ApiErrorResponse(
                false,
                List.of()
        );
        super(400, message, errorResponse);
    }
}
