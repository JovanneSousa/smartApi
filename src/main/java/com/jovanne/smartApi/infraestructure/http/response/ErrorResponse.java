package com.jovanne.smartApi.infraestructure.http.response;

import java.util.List;

public record ErrorResponse(
        int status,
        String message,
        List<String> errors
) {
}
