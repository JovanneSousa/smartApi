package com.jovanne.smartApi.domain.exceptions;

import com.jovanne.smartApi.infraestructure.http.response.ApiErrorResponse;

public class ApiBadRequestException extends ApiClientException {
    public ApiBadRequestException(ApiErrorResponse errorResponse) {
        super(400, "Dados inválidos enviados ao serviço externo", errorResponse);
    }
}
