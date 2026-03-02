package com.company.paymentengine.controller;

import com.company.paymentengine.model.Statement;
import com.company.paymentengine.repository.StatementRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/statements")
@CrossOrigin(origins = "*")
public class StatementController {

    private final StatementRepository statementRepository;

    public StatementController(StatementRepository statementRepository) {
        this.statementRepository = statementRepository;
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<Statement> getStatementByTransaction(@PathVariable UUID transactionId) {
        return statementRepository.findByTransactionId(transactionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
