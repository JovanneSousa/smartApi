package com.jovanne.smartApi.domain.exceptions.financeApiExceptions;

import com.jovanne.smartApi.infraestructure.http.response.FinanceiroErrorResponse;

public class FinanceBadRequestException extends FinanceClientException {
    public FinanceBadRequestException(FinanceiroErrorResponse errorResponse) {
        super(400, "Dados inválidos enviados ao serviço financeiro", errorResponse);
    }
}
