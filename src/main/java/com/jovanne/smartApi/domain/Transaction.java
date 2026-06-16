package com.jovanne.smartApi.domain;

import com.jovanne.smartApi.infraestructure.http.request.TransactionRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Transaction {
//    private String userId = "";
    private String title;
    private long amount;
    private String idCategory;
    private LocalDateTime movementDate;
    private boolean isRecurring;
}
