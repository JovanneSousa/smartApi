package com.jovanne.smartApi.domain.exceptions.apiExceptions;

import com.jovanne.smartApi.infraestructure.http.response.external.ApiErrorResponse;

import java.util.List;

public class ApiBadRequestException extends ApiClientException {
    public ApiBadRequestException(ApiErrorResponse errorResponse) {
        super(400, "Dados inválidos enviados ao serviço externo", errorResponse);
    }
}
