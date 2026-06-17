package com.jovanne.smartApi.domain.exceptions.financeApiExceptions;

import com.jovanne.smartApi.infraestructure.http.response.FinanceiroErrorResponse;

public class FinanceForbbidenException extends FinanceClientException {
    public FinanceForbbidenException(FinanceiroErrorResponse errorResponse) {
        super(403, "Usuário não autenticado no serviço financeiro", errorResponse);
    }
}
