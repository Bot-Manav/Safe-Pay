package com.company.paymentengine.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class Score {
    @Id
    @GeneratedValue
    private UUID id;

    private UUID userAId;

    private UUID userBId;

    private int score;

    private boolean fastTransferEnabled;
}
