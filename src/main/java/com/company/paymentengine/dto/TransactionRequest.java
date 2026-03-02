package com.company.paymentengine.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class TransactionRequest {
    private UUID senderId;
    private UUID receiverId;
    private double amount;
    private String statementText;
}
