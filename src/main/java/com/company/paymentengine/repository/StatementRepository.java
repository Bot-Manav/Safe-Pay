package com.company.paymentengine.repository;

import com.company.paymentengine.model.Statement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StatementRepository extends JpaRepository<Statement, UUID> {
    Optional<Statement> findByTransactionId(UUID transactionId);
}
