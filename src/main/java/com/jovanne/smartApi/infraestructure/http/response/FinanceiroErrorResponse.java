package com.jovanne.smartApi.infraestructure.http.response;

import java.util.List;

public record FinanceiroErrorResponse(
        boolean success,
        List<String> errors
) {
}
