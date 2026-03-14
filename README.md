# Payment Engine

A production-grade, industry-level digital payment infrastructure designed to handle transactions, ledger management, and financial governance with enterprise standards for security and auditability.

## Project Overview

The Payment Engine is a comprehensive trust-aware payment platform. It transforms standard escrow-based systems into a robust financial execution layer, separating decision-making from ledger integrity. It is built to support high-scale, secure, and regulated financial operations.

## Key Features

- **Advanced Transaction Lifecycle**: Secure processing from initiation to settlement.
- **Double-Entry Ledger System**: High-integrity banking core for balance management.
- **Micro-Banking Core**: Integrated execution layer for ledger entries.
- **Case Management**: Advanced dispute resolution and evidence tracking.
- **Risk & Reputation Engine**: AI-ready scoring and risk assessment for every transaction.
- **Multi-Factor Authentication**: Enterprise-level security for sensitive actions.
- **Compliance & KYC**: Built-in AML and identity verification workflows.

## Technology Stack

- **Backend**: Java 17, Spring Boot 3, Spring Data JPA, Spring Security
- **Banking Core**: Specialized standalone service for financial execution.
- **Frontend**: React (Vite-powered) with a premium, responsive UI.
- **Database**: PostgreSQL 15 (Multi-database setup for separation of concerns).
- **Orchestration**: Docker & Docker Compose for rapid deployment.
  

## Feature Architecture & Data Flow

This section details the internal request/response flows between the Payment Engine (Brain) and the Banking Core (Execution).

### 1. User Registration & Onboarding
- **Flow**: Frontend -> `AuthController.register()`
- **Verification**: Backend calls `BankingServiceClient.checkAccountExists()` to validate the provided bank account number.
- **Persistence**: Upon validation, User is saved to DB, and a `Wallet` is created linking the User ID to the Bank Account Number.
- **Response**: Returns the created User profile (sanitized).

### 2. Secure Transaction Lifecycle
The system uses a multi-stage process to ensure transaction integrity.
- **Step 1: Initiation**
    - Request: `POST /api/transactions/initiate` with `TransactionRequest`.
    - Action: Backend validates inputs and creates a `Transaction` record with status `INITIATED`.
- **Step 2: Confirmation & Execution**
    - Request: `POST /api/transactions/confirm` with `ConfirmRequest`.
    - Action: Backend updates status to `COMPLETED` and triggers the `Banking-Service` transfer.
    - Inter-service: Backend calls `banking-service/api/banking/transfer`.

### 3. Banking Core Operations (Double-Entry Ledger)
The `banking-service` acts as the source of truth for all financial movements.
- **Credit/Debit**: `LedgerService` creates matching `LedgerEntry` records.
- **Consistency**: Every transfer involves a symmetric Debit from source and Credit to destination, ensuring the ledger always balances.
- **External Exposure**: The Payment Engine `UserController` fetches real-time balances from the Banking Core via REST.

### 4. Dispute & Case Management
- **Trigger**: `POST /api/disputes/trigger` creates a record linked to a specific `TransactionID`.
- **Admin Review**: Admins use `GET /api/disputes/{id}/details` which aggregates data from:
    - User Repository (Sender/Receiver details)
    - Transaction Repository (Original amount/status)
    - Statement Repository (Transaction metadata)
- **Resolution**: `POST /api/disputes/resolve` updates both the Dispute and Transaction statuses atomically.

---

## Future Roadmap (Implementation Plan)

This section outlines the transformation of the existing system into an industry-level platform.

### Phase 1: Identity & Security
- **Enhanced Auth**: Implementing MFA secrets and account status lifecycles.
- **Role-Based Access**: Specialized roles (Merchant, Compliance Officer, Support Agent).

### Phase 2: Compliance (KYC/AML)
- **Verification Levels**: Multi-tier KYC levels (Basic, Full).
- **AML Service**: Implementation of structuring detection and velocity checks.

### Phase 3: Financial Integrity
- **Authorized vs Settled**: Advanced balance management in the banking service.
- **Reconciliation Engine**: Periodic consistency checks for ledger integrity.

### Phase 4: Operational Governance
- **Dispute (Case) Management**: Lifecycle tracking from evidence submission to resolution.
- **Audit Logs**: Immutable records of every financial and administrative action.
- **Risk Engine**: Rule-based evaluation of transaction risk levels.

### Phase 5: Scalability & Integration
- **API Gateway**: Rate limiting and external API key management for merchants.
- **Notification System**: Event-driven alerts for transaction status and security.

---

## License & Copyright

**© 2026 Manav(Bot-Manav). All Rights Reserved.**

This project is proprietary. No part of this software, including but not limited to source code, compiled binaries, documentation, or design, may be copied, reproduced, distributed, used, or modified without explicit, prior written permission from the owner (**Bot-Manav**).
