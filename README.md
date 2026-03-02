# Payment Engine

A robust payment processing engine built with Spring Boot, React, and PostgreSQL.

## Features

- **Transactions**: Secure processing of financial transactions.
- **Ledger System**: Maintains a reliable record of accounts and balances.
- **Dispute Resolution**: Built-in workflow for handling transaction disputes (Escrow, Mediation).
- **Reputation Tracking**: A scoring system to determine user trustworthiness.
- **Fast Transactions**: Accelerated processing for trusted users.

## Technology Stack

- **Backend**: Java 17, Spring Boot 3, Spring Data JPA, Spring Security
- **Frontend**: React, Vite (port 5173)
- **Database**: PostgreSQL 15 (port 5432)
- **Orchestration**: Docker Compose

## Prerequisites

Ensure you have the following installed on your machine:

- [Docker](https://www.docker.com/products/docker-desktop/) and Docker Compose
- [Java 17](https://adoptium.net/) (for local backend development)
- [Maven](https://maven.apache.org/) (for local backend development)
- [Node.js](https://nodejs.org/) (for local frontend development)



## Project Structure

```text
payment-engine/
├── backend/            # Spring Boot application containing API controllers, services, models, and repositories.
├── frontend/           # React application containing UI components, routing, and styling.
├── db/                 # Database initialization scripts and volumes.
└── docker-compose.yml  # Orchestration configuration to run the entire stack.
```

## License

© 2026 Safe-Pay. All Rights Reserved.

This project is proprietary. No part of this software, including but not limited to source code, compiled binaries, documentation, or design, may be copied, reproduced, distributed, used, or modified without explicit, prior written permission from the repository owner Dedakiya Manav(Bot-Manav).
