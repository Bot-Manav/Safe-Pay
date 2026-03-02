package com.company.paymentengine.controller;

import com.company.paymentengine.dto.ConfirmRequest;
import com.company.paymentengine.dto.TransactionRequest;
import com.company.paymentengine.model.Transaction;
import com.company.paymentengine.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<Transaction> initiateTransaction(@RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.initiateTransaction(request));
    }

    @PostMapping("/confirm")
    public ResponseEntity<Transaction> confirmStatement(@RequestBody ConfirmRequest request) {
        return ResponseEntity.ok(transactionService.confirmStatement(request));
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable UUID id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }
}