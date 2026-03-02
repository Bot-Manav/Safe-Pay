package com.company.paymentengine.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class DisputeRequest {
    private UUID transactionId;
    private UUID initiatorId;
}
