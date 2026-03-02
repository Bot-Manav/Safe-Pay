package com.company.paymentengine.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ResolveDisputeRequest {
    private UUID disputeId;
    private String adminNote;
    private boolean refundToSender;
}
