package com.company.paymentengine.model;

import com.company.paymentengine.enums.DisputeStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class Dispute {
    @Id
    @GeneratedValue
    private UUID id;

    private UUID transactionId;

    private UUID initiatorId;

    @Enumerated(EnumType.STRING)
    private DisputeStatus status;

    private String adminNote;

    private LocalDateTime createdAt;
}
