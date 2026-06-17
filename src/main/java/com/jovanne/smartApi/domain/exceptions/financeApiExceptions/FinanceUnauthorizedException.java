package com.jovanne.smartApi.domain.exceptions.financeApiExceptions;

import com.jovanne.smartApi.infraestructure.http.response.FinanceiroErrorResponse;

public class FinanceUnauthorizedException extends FinanceClientException {
    public FinanceUnauthorizedException(FinanceiroErrorResponse errorResponse) {
        super(401, "Não autorizado no serviço financeiro", errorResponse);
    }
}
