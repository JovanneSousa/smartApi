package com.jovanne.smartApi.infraestructure.http.response.internal;


import com.jovanne.smartApi.application.tool.ToolResultHolder;

public record InternalApiResponse<T >(boolean success, T data) {
    public static InternalApiResponse fromToolResult(ToolResultHolder holder) {
        return new InternalApiResponse(
                holder.get().success(),
                holder.get().message()
        );
    }
}
