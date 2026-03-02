package com.company.paymentengine.service;

import com.company.paymentengine.dto.*;
import com.company.paymentengine.enums.*;
import com.company.paymentengine.model.*;
import com.company.paymentengine.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final LedgerRepository ledgerRepository;
    private final StatementRepository statementRepository;
    private final DisputeRepository disputeRepository;
    private final ScoreRepository scoreRepository;

    private static final int FAST_TRANSFER_THRESHOLD = 3;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository,
            LedgerRepository ledgerRepository, StatementRepository statementRepository,
            DisputeRepository disputeRepository, ScoreRepository scoreRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.ledgerRepository = ledgerRepository;
        this.statementRepository = statementRepository;
        this.disputeRepository = disputeRepository;
        this.scoreRepository = scoreRepository;
    }

    @Transactional
    public Transaction initiateTransaction(TransactionRequest request) {
        User sender = userRepository.findById(request.getSenderId()).orElseThrow();
        User receiver = userRepository.findById(request.getReceiverId()).orElseThrow();

        if (sender.getBalance() < request.getAmount()) {
            throw new RuntimeException("Insufficient balance");
        }

        // Check score
        Optional<Score> scoreOpt = scoreRepository.findByUserAIdAndUserBId(sender.getId(), receiver.getId())
                .or(() -> scoreRepository.findByUserAIdAndUserBId(receiver.getId(), sender.getId()));

        boolean canFastTransfer = scoreOpt.isPresent() && scoreOpt.get().isFastTransferEnabled();

        Transaction tx = new Transaction();
        tx.setSenderId(sender.getId());
        tx.setReceiverId(receiver.getId());
        tx.setAmount(request.getAmount());
        tx.setCreatedAt(LocalDateTime.now());
        tx.setFastTransaction(canFastTransfer);

        if (canFastTransfer) {
            sender.setBalance(sender.getBalance() - request.getAmount());
            receiver.setBalance(receiver.getBalance() + request.getAmount());
            userRepository.save(sender);
            userRepository.save(receiver);

            tx.setStatus(TransactionStatus.COMPLETED);
            transactionRepository.save(tx);

            // Fast transaction ledger record
            createLedgerEntry(tx.getId(), receiver.getId(), LedgerEntryType.CREDIT, request.getAmount(),
                    LedgerStatus.RELEASED);
            return tx;
        } else {
            // Escrow flow
            sender.setBalance(sender.getBalance() - request.getAmount());
            userRepository.save(sender);

            tx.setStatus(TransactionStatus.PENDING);
            tx.setTimeoutAt(LocalDateTime.now().plusHours(24));
            transactionRepository.save(tx);

            // Ledger
            createLedgerEntry(tx.getId(), sender.getId(), LedgerEntryType.DEBIT, request.getAmount(),
                    LedgerStatus.HELD);

            // Statement
            Statement stmt = new Statement();
            stmt.setTransactionId(tx.getId());
            stmt.setText(request.getStatementText());
            stmt.setSenderConfirmed(true);
            stmt.setReceiverConfirmed(false);
            statementRepository.save(stmt);

            return tx;
        }
    }

    @Transactional
    public Transaction confirmStatement(ConfirmRequest request) {
        Transaction tx = transactionRepository.findById(request.getTransactionId()).orElseThrow();

        if (tx.getStatus() != TransactionStatus.PENDING) {
            throw new RuntimeException("Transaction not pending");
        }

        if (!tx.getReceiverId().equals(request.getUserId())) {
            throw new RuntimeException("Only receiver can confirm");
        }

        Statement stmt = statementRepository.findByTransactionId(tx.getId()).orElseThrow();
        stmt.setReceiverConfirmed(true);
        statementRepository.save(stmt);

        // Update tx
        tx.setStatus(TransactionStatus.COMPLETED);
        transactionRepository.save(tx);

        // Update ledger
        Ledger ledger = ledgerRepository.findAll().stream()
                .filter(l -> l.getTransactionId().equals(tx.getId()) && l.getStatus() == LedgerStatus.HELD)
                .findFirst().orElseThrow();
        ledger.setStatus(LedgerStatus.RELEASED);
        ledgerRepository.save(ledger);

        // Transfer funds
        User receiver = userRepository.findById(tx.getReceiverId()).orElseThrow();
        receiver.setBalance(receiver.getBalance() + tx.getAmount());
        userRepository.save(receiver);

        // Update score
        updateScore(tx.getSenderId(), tx.getReceiverId());

        return tx;
    }

    private void updateScore(UUID userA, UUID userB) {
        Optional<Score> scoreOpt = scoreRepository.findByUserAIdAndUserBId(userA, userB)
                .or(() -> scoreRepository.findByUserAIdAndUserBId(userB, userA));

        Score score = scoreOpt.orElseGet(() -> {
            Score s = new Score();
            s.setUserAId(userA);
            s.setUserBId(userB);
            s.setScore(0);
            return s;
        });

        score.setScore(score.getScore() + 1);
        if (score.getScore() >= FAST_TRANSFER_THRESHOLD) {
            score.setFastTransferEnabled(true);
        }
        scoreRepository.save(score);
    }

    @Transactional
    public Dispute triggerDispute(DisputeRequest request) {
        Transaction tx = transactionRepository.findById(request.getTransactionId()).orElseThrow();
        if (tx.getStatus() != TransactionStatus.PENDING) {
            throw new RuntimeException("Can only dispute pending transactions");
        }

        tx.setStatus(TransactionStatus.DISPUTED);
        transactionRepository.save(tx);

        // Freeze ledger
        Ledger ledger = ledgerRepository.findAll().stream()
                .filter(l -> l.getTransactionId().equals(tx.getId()) && l.getStatus() == LedgerStatus.HELD)
                .findFirst().orElseThrow();
        ledger.setStatus(LedgerStatus.FROZEN);
        ledgerRepository.save(ledger);

        Dispute dispute = new Dispute();
        dispute.setTransactionId(tx.getId());
        dispute.setInitiatorId(request.getInitiatorId());
        dispute.setStatus(DisputeStatus.PENDING);
        dispute.setCreatedAt(LocalDateTime.now());
        return disputeRepository.save(dispute);
    }

    @Transactional
    public Dispute resolveDispute(ResolveDisputeRequest request) {
        Dispute dispute = disputeRepository.findById(request.getDisputeId()).orElseThrow();
        if (dispute.getStatus() != DisputeStatus.PENDING) {
            throw new RuntimeException("Dispute already resolved");
        }

        Transaction tx = transactionRepository.findById(dispute.getTransactionId()).orElseThrow();
        Ledger ledger = ledgerRepository.findAll().stream()
                .filter(l -> l.getTransactionId().equals(tx.getId()) && l.getStatus() == LedgerStatus.FROZEN)
                .findFirst().orElseThrow();

        dispute.setAdminNote(request.getAdminNote());
        dispute.setStatus(DisputeStatus.RESOLVED);
        disputeRepository.save(dispute);

        if (request.isRefundToSender()) {
            tx.setStatus(TransactionStatus.REVERSED);
            ledger.setStatus(LedgerStatus.REVERSED);
            User sender = userRepository.findById(tx.getSenderId()).orElseThrow();
            sender.setBalance(sender.getBalance() + tx.getAmount());
            userRepository.save(sender);
        } else {
            tx.setStatus(TransactionStatus.COMPLETED);
            ledger.setStatus(LedgerStatus.RELEASED);
            User receiver = userRepository.findById(tx.getReceiverId()).orElseThrow();
            receiver.setBalance(receiver.getBalance() + tx.getAmount());
            userRepository.save(receiver);
        }
        transactionRepository.save(tx);
        ledgerRepository.save(ledger);
        return dispute;
    }

    private void createLedgerEntry(UUID txId, UUID accountId, LedgerEntryType type, double amount,
            LedgerStatus status) {
        Ledger ledger = new Ledger();
        ledger.setTransactionId(txId);
        ledger.setAccountId(accountId);
        ledger.setEntryType(type.name());
        ledger.setAmount(amount);
        ledger.setStatus(status);
        ledgerRepository.save(ledger);
    }

    @org.springframework.scheduling.annotation.Scheduled(fixedRate = 60000)
    @Transactional
    public void processTimeouts() {
        List<Transaction> pendingTxs = transactionRepository.findAll().stream()
                .filter(tx -> tx.getStatus() == TransactionStatus.PENDING &&
                        tx.getTimeoutAt() != null &&
                        tx.getTimeoutAt().isBefore(LocalDateTime.now()))
                .toList();

        for (Transaction tx : pendingTxs) {
            tx.setStatus(TransactionStatus.REVERSED);
            transactionRepository.save(tx);

            // Revert ledger
            ledgerRepository.findAll().stream()
                    .filter(l -> l.getTransactionId().equals(tx.getId()) && l.getStatus() == LedgerStatus.HELD)
                    .findFirst()
                    .ifPresent(ledger -> {
                        ledger.setStatus(LedgerStatus.REVERSED);
                        ledgerRepository.save(ledger);
                        User sender = userRepository.findById(tx.getSenderId()).orElseThrow();
                        sender.setBalance(sender.getBalance() + tx.getAmount());
                        userRepository.save(sender);
                    });
        }
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(UUID id) {
        return transactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found"));
    }
}