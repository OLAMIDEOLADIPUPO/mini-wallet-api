# 💳 Mini Wallet Ledger API

A production-quality RESTful wallet management API built with Spring Boot, designed to simulate core fintech operations — wallet creation, fund deposits, withdrawals, and peer-to-peer transfers — with full transaction history and robust business rule enforcement.

> Built from scratch as a deliberate learning project, applying real-world fintech engineering principles: immutable transaction records, defensive balance management, soft deletes for audit trail preservation, and clean layered architecture.

---

## 🚀 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0 |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Validation | Jakarta Bean Validation |
| Build Tool | Maven |

---

## 🏗️ Architecture

The project follows a strict **layered architecture**:

```
Controller → Service Interface → Service Implementation → Repository → Database
```

- **Controllers** handle HTTP concerns only — no business logic
- **Service Interfaces** define contracts, decoupling implementation from consumption
- **Service Implementations** own all business rules and transaction logic
- **Repositories** handle all database interaction via Spring Data JPA
- **DTOs** ensure the internal data model is never exposed directly to the API layer

---

## 💡 Key Engineering Decisions

### BigDecimal for Money
All monetary values use `BigDecimal` — never `double` or `float`. Floating-point arithmetic introduces rounding errors that are unacceptable in financial systems.

### Immutable Transaction Records
The `Transaction` entity has no setters. Once a transaction is recorded, it cannot be modified. This is intentional — financial audit trails must be tamper-proof.

### Defensive Balance Management
The `Wallet` entity owns its own balance logic via `credit()` and `debit()` methods. The wallet protects its own state — it will never allow its balance to go negative, regardless of what calls it.

### Soft Delete
Wallets are never hard-deleted from the database. Deactivating a wallet sets `isActive = false`, preserving the complete transaction history for audit purposes. This mirrors how real financial institutions handle account closure.

### Transactional Integrity
All fund movement operations (`deposit`, `withdraw`, `transfer`) are wrapped in `@Transactional`. If any step fails mid-operation — for example, crediting the receiver after debiting the sender — the entire operation rolls back, preventing money from disappearing.

### Global Exception Handling
A `@ControllerAdvice` global exception handler intercepts all custom business exceptions and returns structured JSON error responses with appropriate HTTP status codes, rather than exposing raw stack traces.

---

## 📦 Project Structure

```
src/main/java/com/olamide/miniwalletapi/
├── Controller/
│   ├── WalletController.java
│   └── TransactionController.java
├── DTO/
│   ├── UserRequestDTO.java
│   ├── UserResponseDTO.java
│   ├── DepositRequestDTO.java
│   ├── WithdrawRequestDTO.java
│   ├── TransferRequestDTO.java
│   └── TransactionResponseDTO.java
├── Exceptions/
│   ├── GlobalExceptionHandler.java
│   ├── ErrorResponse.java
│   ├── WalletNotFoundException.java
│   ├── InsufficientFundsException.java
│   ├── InvalidAmountException.java
│   ├── InvalidWalletDetailsException.java
│   └── WalletDeactivatedException.java
├── Models/
│   ├── Wallet.java
│   └── Transaction.java
├── Repository/
│   ├── WalletRepository.java
│   └── TransactionRepository.java
├── Service/
│   ├── WalletService.java
│   ├── TransactionService.java
│   └── ServiceImpl/
│       ├── WalletServiceImpl.java
│       └── TransactionServiceImpl.java
└── TransactionType.java (enum)
```

---

## ⚙️ Getting Started

### Prerequisites
- Java 21+
- PostgreSQL running locally
- Maven

### Setup

1. **Clone the repository**
```bash
git clone https://github.com/yourusername/mini-wallet-api.git
cd mini-wallet-api
```

2. **Configure your database**

Copy the example properties file and fill in your credentials:
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Edit `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD_HERE
spring.jpa.hibernate.ddl-auto=update
```

3. **Run the application**
```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

---

## 📡 API Endpoints

### Wallet Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/mini_wallet/api` | Create a new wallet |
| `GET` | `/mini_wallet/api/{id}` | Find wallet by ID |
| `GET` | `/mini_wallet/api/number/{walletNumber}` | Find wallet by wallet number |
| `DELETE` | `/mini_wallet/api/{id}` | Deactivate a wallet (soft delete) |

### Transaction Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/transactions/deposit` | Deposit funds into a wallet |
| `POST` | `/api/transactions/withdraw` | Withdraw funds from a wallet |
| `POST` | `/api/transactions/transfer` | Transfer funds between wallets |
| `GET` | `/api/transactions/{walletId}/history` | Get full transaction history for a wallet |
| `GET` | `/api/transactions/transactions/{transactionId}` | Get a specific transaction by ID |

---

## 📋 Request & Response Examples

### Create Wallet
```http
POST /mini_wallet/api
Content-Type: application/json

{
  "ownerName": "Olamide"
}
```
```json
{
  "ownerName": "Olamide",
  "walletNumber": "550e8400-e29b-41d4-a716-446655440000",
  "balance": 0
}
```

### Deposit
```http
POST /api/transactions/deposit
Content-Type: application/json

{
  "destinationWalletId": 1,
  "amount": 5000
}
```
```json
{
  "sourceWalletId": null,
  "destinationWalletId": 1,
  "amount": 5000,
  "type": "DEPOSIT",
  "timestamp": "2026-05-20T10:00:00Z"
}
```

### Transfer
```http
POST /api/transactions/transfer
Content-Type: application/json

{
  "sourceWalletId": 1,
  "destinationWalletId": 2,
  "amount": 2000
}
```

---

## 🔒 Business Rules

- ✅ Wallet balance can never go negative
- ✅ Cannot withdraw or transfer more than available balance
- ✅ Cannot deposit, withdraw, or transfer zero or negative amounts
- ✅ Wallet numbers are unique (UUID) and system-generated
- ✅ Deactivated wallets cannot send or receive funds
- ✅ Transaction history is permanently preserved — even after wallet deactivation
- ✅ Transfers are atomic — both wallets update or neither does

---

## ❌ Error Responses

All errors return a structured JSON response:

```json
{
  "error": "INSUFFICIENT_FUNDS",
  "message": "Amount greater than balance",
  "timestamp": "2026-05-20T10:00:00"
}
```

| Error Code | HTTP Status | Meaning |
|---|---|---|
| `WALLET_NOT_FOUND` | 404 | Wallet ID does not exist |
| `INSUFFICIENT_FUNDS` | 400 | Withdrawal/transfer exceeds balance |
| `INVALID_AMOUNT` | 400 | Amount is zero or negative |
| `INVALID_WALLET_DETAILS` | 400 | Missing or invalid wallet information |
| `WALLET_DEACTIVATED` | 400 | Wallet exists but is deactivated |

---

## 🛣️ Roadmap

- [ ] Spring Security + JWT authentication
- [ ] Lombok for boilerplate reduction
- [ ] MapStruct for DTO mapping
- [ ] Unit and integration tests
- [ ] Docker containerisation
- [ ] Database migrations with Flyway

---

## 👨‍💻 Author

**Olamide**
Backend Developer — building real-world systems one layer at a time.
