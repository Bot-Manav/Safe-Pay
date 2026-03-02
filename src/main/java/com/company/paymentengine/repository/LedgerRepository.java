package com.company.paymentengine.repository;

import com.company.paymentengine.model.Ledger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LedgerRepository extends JpaRepository<Ledger, UUID> {
}