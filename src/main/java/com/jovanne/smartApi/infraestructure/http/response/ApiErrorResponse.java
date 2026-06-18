package com.jovanne.smartApi.infraestructure.http.response;

import java.util.List;

public record ApiErrorResponse(
        boolean success,
        List<String> errors
) {
}
