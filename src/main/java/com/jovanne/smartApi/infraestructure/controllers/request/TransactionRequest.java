package com.jovanne.smartApi.infraestructure.controllers.request;

import com.jovanne.smartApi.application.input.PersistTransactionInput;
import com.jovanne.smartApi.domain.Category;

public record TransactionRequest(String description, Category category, long amount) {
    public PersistTransactionInput toInput() {
        return new PersistTransactionInput(
                this.description,
                this.category,
                this.amount
        );
    }
}
