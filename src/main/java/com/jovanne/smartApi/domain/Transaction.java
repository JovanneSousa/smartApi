package com.jovanne.smartApi.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class Transaction {
    private String transactionId;
    private String description;
    private Category category;
    private long amount;

    public Transaction(String description, Category category, long amount) {
        this.transactionId = UUID.randomUUID().toString();
        this.description = description;
        this.category = category;
        this.amount = amount;
    }
}
