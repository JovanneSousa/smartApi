package com.jovanne.smartApi.domain.exceptions.financeApiExceptions;

import com.jovanne.smartApi.infraestructure.http.response.FinanceiroErrorResponse;

public class FinanceiroForbbidenException extends FinanceiroClientException{
    public FinanceiroForbbidenException(FinanceiroErrorResponse errorResponse) {
        super(403, "Usuário não autenticado no serviço financeiro", errorResponse);
    }
}
