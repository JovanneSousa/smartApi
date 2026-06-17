package com.jovanne.smartApi.infraestructure.http.response;

import com.jovanne.smartApi.application.tool.ToolResultHolder;

import java.util.List;

public record ErrorResponse(
        int status,
        String message,
        List<String> errors
) {
    public static ErrorResponse fromToolResultHolder(ToolResultHolder holder) {
        return new ErrorResponse(
                holder.get().statusCode(),
                holder.get().message(),
                holder.get().errors()
        );
    }
}
