package com.jovanne.smartApi.application.tool;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class ToolResultHolder {

    private ToolResult result;

    public void set(ToolResult result) { this.result = result; }
    public ToolResult get() { return result; }

}
