package com.jovanne.smartApi.application.input;

import com.jovanne.smartApi.domain.Category;
import org.springframework.ai.tool.annotation.ToolParam;

public record PersistTransactionInput(
        @ToolParam(description = "Descrição do gasto") String description,
        @ToolParam(description = "Categoria da transação") Category category,
        @ToolParam(description = "Valor da transação") long amount) {
}
