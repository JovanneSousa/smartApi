package com.jovanne.smartApi.infraestructure.http.response.external;

import com.jovanne.smartApi.application.tool.ToolResultHolder;
import com.jovanne.smartApi.domain.exceptions.apiExceptions.ApiClientException;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public record ErrorResponse(
        int status,
        String message,
        List<String> errors
) implements BaseResponse {
    public static ErrorResponse fromToolResultHolder(ToolResultHolder holder, String chatError) {
        var errors = new ArrayList<String>(holder.get().errors());
        errors.add(chatError);
        return new ErrorResponse(
                holder.get().statusCode(),
                holder.get().message(),
                errors
        );
    }

    public static ErrorResponse fromApiExcetipn(ApiClientException ex) {
        return new ErrorResponse(
                ex.getStatusCode(),
                ex.getMessage(),
                ex.getErrorResponse() != null ?
                        ex.getErrorResponse().errors() :
                        List.of()
        );
    }

    @Override
    public boolean isValid() {
        return message != null && errors != null;
    }
}
