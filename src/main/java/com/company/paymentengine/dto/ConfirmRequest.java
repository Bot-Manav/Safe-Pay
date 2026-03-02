package com.company.paymentengine.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ConfirmRequest {
    private UUID transactionId;
    private UUID userId;
}
