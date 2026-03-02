package com.company.paymentengine.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class Ledger {
    @Id
    @GeneratedValue
    private UUID id;

    private UUID transactionId;

    private UUID accountId;

    private String entryType;

    private double amount;

    @Enumerated(EnumType.STRING)
    private com.company.paymentengine.enums.LedgerStatus status;
}