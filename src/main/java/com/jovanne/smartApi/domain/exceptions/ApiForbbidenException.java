package com.jovanne.smartApi.domain.exceptions;

import com.jovanne.smartApi.infraestructure.http.response.ApiErrorResponse;

public class ApiForbbidenException extends ApiClientException {
    public ApiForbbidenException(ApiErrorResponse errorResponse) {
        super(403, "Usuário não autenticado no serviço externo", errorResponse);
    }
}
