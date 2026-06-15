package com.jovanne.smartApi.infraestructure.persistense.entity;

import com.jovanne.smartApi.domain.Category;
import com.jovanne.smartApi.domain.Transaction;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity {

    @Id
    private UUID id;

    private String description;
    private long amount;

    @Enumerated(EnumType.STRING)
    private Category category;

    public static TransactionEntity fromTransaction(Transaction base) {
        return new TransactionEntity(
                UUID.fromString(base.getTransactionId()),
                base.getDescription(),
                base.getAmount(),
                base.getCategory());
    }

    public Transaction toDomain() {
        return new Transaction(
                this.id.toString(),
                this.description,
                this.category,
                this.amount
        );
    }
}
