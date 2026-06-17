package com.jovanne.smartApi.application.tool;

import java.util.List;

public record ToolResult(boolean success, String message, List<String> errors) {
    public static ToolResult ok(String message) {
        return new ToolResult(true, message, List.of());
    }

    public static ToolResult error(String message, List<String> errors) {
        return new ToolResult(false, message, errors);
    }
}
