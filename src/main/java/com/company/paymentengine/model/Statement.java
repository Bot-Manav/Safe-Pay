package com.company.paymentengine.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class Statement {
    @Id
    @GeneratedValue
    private UUID id;

    private UUID transactionId;

    private String text;

    private boolean senderConfirmed;

    private boolean receiverConfirmed;
}
