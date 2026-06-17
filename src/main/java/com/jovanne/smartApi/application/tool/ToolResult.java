package com.jovanne.smartApi.application.tool;

import java.util.List;

public record ToolResult(int statusCode, boolean success, String message, List<String> errors) {
    public static ToolResult ok(int statusCode,String message) {
        return new ToolResult(statusCode, true, message, List.of());
    }

    public static ToolResult error(int statusCode, String message, List<String> errors) {
        return new ToolResult(statusCode,false, message, errors);
    }
}
