package com.company.paymentengine.controller;

import com.company.paymentengine.dto.DisputeRequest;
import com.company.paymentengine.dto.ResolveDisputeRequest;
import com.company.paymentengine.model.Dispute;
import com.company.paymentengine.repository.DisputeRepository;
import com.company.paymentengine.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disputes")
@CrossOrigin(origins = "*")
public class DisputeController {

    private final TransactionService transactionService;
    private final DisputeRepository disputeRepository;

    public DisputeController(TransactionService transactionService, DisputeRepository disputeRepository) {
        this.transactionService = transactionService;
        this.disputeRepository = disputeRepository;
    }

    @GetMapping
    public ResponseEntity<List<Dispute>> getAllDisputes() {
        return ResponseEntity.ok(disputeRepository.findAll());
    }

    @PostMapping("/trigger")
    public ResponseEntity<Dispute> triggerDispute(@RequestBody DisputeRequest request) {
        return ResponseEntity.ok(transactionService.triggerDispute(request));
    }

    @PostMapping("/resolve")
    public ResponseEntity<Dispute> resolveDispute(@RequestBody ResolveDisputeRequest request) {
        return ResponseEntity.ok(transactionService.resolveDispute(request));
    }
}
