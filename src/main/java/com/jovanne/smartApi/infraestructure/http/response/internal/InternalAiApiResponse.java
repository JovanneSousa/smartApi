package com.jovanne.smartApi.infraestructure.http.response.internal;

import com.jovanne.smartApi.application.tool.ToolResult;

public record InternalAiApiResponse<T>(boolean success, T data, String aiMessage){
    public static InternalAiApiResponse fromToolResult(ToolResult result, String aiMessage) {
        return new InternalAiApiResponse(
                result.success(),
                result.message(),
                aiMessage
        );
    }
}
