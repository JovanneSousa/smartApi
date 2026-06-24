package com.jovanne.smartApi.domain.exceptions.apiExceptions;

import com.jovanne.smartApi.infraestructure.http.response.external.ApiErrorResponse;

public class ApiForbbidenException extends ApiClientException {
    public ApiForbbidenException(ApiErrorResponse errorResponse) {
        super(403, "Usuário não autenticado no serviço externo", errorResponse);
    }
}
