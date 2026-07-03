package com.jovanne.smartApi.application.tool;

import org.springframework.stereotype.Component;

@Component
public class ToolResultHolder {

    private static final ThreadLocal<ToolResult> RESULT = new ThreadLocal<>();

    public void set(ToolResult result) { RESULT.set(result); }
    public ToolResult get() { return RESULT.get(); }

    public void clear() {
        RESULT.remove();
    }
}
