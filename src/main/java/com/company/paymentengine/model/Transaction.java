package com.company.paymentengine.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class Transaction {
    @Id
    @GeneratedValue
    private UUID id;

    private UUID senderId;

    private UUID receiverId;

    private double amount;

    @Enumerated(EnumType.STRING)
    private com.company.paymentengine.enums.TransactionStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime timeoutAt;

    private boolean isFastTransaction;
}