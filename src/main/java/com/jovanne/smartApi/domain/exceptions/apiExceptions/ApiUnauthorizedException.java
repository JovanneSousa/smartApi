package com.jovanne.smartApi.domain.exceptions.apiExceptions;

import com.jovanne.smartApi.infraestructure.http.response.external.ApiErrorResponse;

public class ApiUnauthorizedException extends ApiClientException {
    public ApiUnauthorizedException(ApiErrorResponse errorResponse) {
        super(401, "Não autorizado no serviço externo", errorResponse);
    }
}
