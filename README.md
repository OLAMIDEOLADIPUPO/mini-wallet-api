# 💳 Mini Wallet Ledger API

A production-quality RESTful wallet management API built with Spring Boot, designed to simulate core fintech operations — user registration, authentication, wallet management, fund deposits, withdrawals, and peer-to-peer transfers — with full transaction history, JWT-based security, and robust business rule enforcement.

Built from scratch as a deliberate learning project, applying real-world fintech engineering principles: stateless JWT authentication, role-based access control, immutable transaction records, defensive balance management, soft deletes for audit trail preservation, and clean layered architecture.

---

## 🚀 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0 |
| Security | Spring Security 7 + JWT (JJWT 0.12) |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Validation | Jakarta Bean Validation |
| Build Tool | Maven |

---

## 🏗️ Architecture

The project follows a strict layered architecture:

```
Controller → Service Interface → Service Implementation → Repository → Database
```

- **Controllers** handle HTTP concerns and access control only — no business logic
- **Service Interfaces** define contracts, decoupling implementation from consumption
- **Service Implementations** own all business rules and transaction logic
- **Repositories** handle all database interaction via Spring Data JPA
- **DTOs** ensure the internal domain model is never exposed directly to the API layer
- **SecurityUtils** provides a shared utility for extracting the authenticated user from the `SecurityContextHolder`

---

## 🔒 Security Architecture

Authentication is handled via **stateless JWT tokens**. No sessions are created or stored server-side.

### Authentication Flow

```
POST /auth/login
       ↓
AuthenticationManager → DaoAuthenticationProvider
       ↓
UserDetailsService loads User from DB
       ↓
BCrypt verifies password
       ↓
JwtService generates signed token
       ↓
Token returned to client
```

### Request Flow (subsequent requests)

```
Request with Authorization: Bearer <token>
       ↓
JwtAuthenticationFilter (OncePerRequestFilter)
       ↓
JwtService validates token signature + expiry
       ↓
User loaded and placed in SecurityContextHolder
       ↓
Controller / Service reads authenticated user
```

### Role Model

| Role | Capabilities |
|---|---|
| `USER` | Register, login, view own wallet, deposit, withdraw, transfer, view own transaction history |
| `ADMIN` | View all wallets, view any transaction by ID, view any wallet's transaction history |

---

## 💡 Key Engineering Decisions

**BigDecimal for Money**
All monetary values use `BigDecimal` — never `double` or `float`. Floating-point arithmetic introduces rounding errors that are unacceptable in financial systems.

**Stateless JWT Authentication**
Every request is authenticated independently via a signed JWT token. No server-side sessions means the API scales horizontally without shared session storage.

**Ownership Enforcement at the Service Layer**
All user-facing operations derive the acting wallet from the authenticated principal via `SecurityContextHolder` — never from a client-supplied ID. A user cannot perform operations on another user's wallet by guessing an ID.

**Immutable Transaction Records**
The `Transaction` entity has no setters. Once a transaction is recorded, it cannot be modified. Financial audit trails must be tamper-proof.

**Defensive Balance Management**
The `Wallet` entity owns its own balance logic via `credit()` and `debit()` methods. The wallet protects its own state — it will never allow its balance to go negative, regardless of what calls it.

**Soft Delete**
Wallets are never hard-deleted from the database. Deactivating a wallet sets `isActive = false`, preserving the complete transaction history for audit purposes. This mirrors how real financial institutions handle account closure.

**Transactional Integrity**
All fund movement operations (deposit, withdraw, transfer) are wrapped in `@Transactional`. If any step fails mid-operation, the entire operation rolls back, preventing money from disappearing.

**Global Exception Handling**
A `@ControllerAdvice` global exception handler intercepts all custom business exceptions and returns structured JSON error responses with appropriate HTTP status codes.

---

## 📦 Project Structure

```
src/main/java/com/olamide/miniwalletapi/
├── Configuration/
│   ├── SecurityConfiguration.java
│   ├── JwtAuthenticationFilter.java
│   ├── JwtService.java
│   ├── SecurityUtils.java
│   └── DataSeeder.java
├── Controller/
│   ├── AuthController.java
│   ├── WalletController.java
│   └── TransactionController.java
├── DTO/
│   ├── RegisterUserDTO.java
│   ├── LoginRequestDTO.java
│   ├── AuthResponseDTO.java
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
│   ├── User.java
│   ├── Wallet.java
│   ├── Transaction.java
│   └── Role.java (enum)
├── Repository/
│   ├── UserRepository.java
│   ├── WalletRepository.java
│   └── TransactionRepository.java
└── Service/
    ├── AuthService.java
    ├── WalletService.java
    ├── TransactionService.java
    └── ServiceImpl/
        ├── AuthServiceImpl.java
        ├── CustomerUserDetailsServiceImpl.java
        ├── WalletServiceImpl.java
        └── TransactionServiceImpl.java
```

---

## ⚙️ Getting Started

### Prerequisites

- Java 21+
- PostgreSQL running locally
- Maven

### Setup

1. Clone the repository

```bash
git clone https://github.com/yourusername/mini-wallet-api.git
cd mini-wallet-api
```

2. Configure your database and JWT secret in `application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/wallet_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

jwt.secret=your-base64-encoded-256-bit-secret-key-here
jwt.expiration=86400000
```

> ⚠️ Never commit real credentials or your JWT secret to version control. Use environment variables or a `.env` file in production.

3. Run the application

```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`.

On first startup, an admin user is automatically seeded:
- Email: `admin@wallet.com`
- Password: `admin123`

---

## 📡 API Endpoints

### Auth Endpoints (public)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/auth/register` | Register a new user (creates wallet automatically) |
| POST | `/auth/login` | Login and receive a JWT token |

### Wallet Endpoints

| Method | Endpoint | Role | Description |
|---|---|---|---|
| GET | `/mini_wallet/api/me` | USER | Get your own wallet |
| GET | `/mini_wallet/api/{id}` | USER / ADMIN | Get wallet by ID (USER can only access their own) |
| GET | `/mini_wallet/api` | ADMIN | Get all wallets |
| DELETE | `/mini_wallet/api/{id}` | ADMIN | Deactivate a wallet (soft delete) |

### Transaction Endpoints

| Method | Endpoint | Role | Description |
|---|---|---|---|
| POST | `/api/transactions/deposit` | USER | Deposit into your wallet |
| POST | `/api/transactions/withdraw` | USER | Withdraw from your wallet |
| POST | `/api/transactions/transfer` | USER | Transfer to another wallet |
| GET | `/api/transactions/history` | USER | Get your own transaction history |
| GET | `/api/transactions/{walletId}/history` | ADMIN | Get any wallet's transaction history |
| GET | `/api/transactions/{transactionId}` | ADMIN | Get a specific transaction by ID |

---

## 📋 Request & Response Examples

### Register

```http
POST /auth/register
Content-Type: application/json

{
  "email": "ola@test.com",
  "password": "password123"
}
```

```json
{
  "message": "Registration successful! Wallet Created",
  "email": "ola@test.com",
  "walletNumber": "550e8400-e29b-41d4-a716-446655440000",
  "token": null
}
```

### Login

```http
POST /auth/login
Content-Type: application/json

{
  "email": "ola@test.com",
  "password": "password123"
}
```

```json
{
  "message": "Login successful",
  "email": "ola@test.com",
  "walletNumber": "550e8400-e29b-41d4-a716-446655440000",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Authenticated Request

All protected endpoints require the token in the Authorization header:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Transfer

```http
POST /api/transactions/transfer
Authorization: Bearer <token>
Content-Type: application/json

{
  "destinationWalletId": 2,
  "amount": 2000
}
```

```json
{
  "sourceWalletId": 1,
  "destinationWalletId": 2,
  "amount": 2000,
  "transactionType": "TRANSFER",
  "timestamp": "2026-06-08T10:00:00Z"
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
- ✅ A user can only operate on their own wallet — ownership is enforced at the service layer, not just the controller
- ✅ Passwords are hashed with BCrypt before storage — plain-text passwords are never persisted

---

## ❌ Error Responses

All errors return a structured JSON response:

```json
{
  "error": "INSUFFICIENT_FUNDS",
  "message": "Amount greater than balance",
  "timestamp": "2026-06-08T10:00:00"
}
```

| Error Code | HTTP Status | Meaning |
|---|---|---|
| `WALLET_NOT_FOUND` | 404 | Wallet ID does not exist |
| `INSUFFICIENT_FUNDS` | 400 | Withdrawal/transfer exceeds balance |
| `INVALID_AMOUNT` | 400 | Amount is zero or negative |
| `INVALID_WALLET_DETAILS` | 400 | Missing or invalid wallet information |
| `WALLET_DEACTIVATED` | 400 | Wallet exists but is deactivated |
| `INVALID_TRANSACTION` | 400 | Transfer attempted to same wallet |

---

## 🛣️ Roadmap

- [x] Core wallet and transaction operations
- [x] Spring Security + JWT authentication
- [x] Role-based access control (USER / ADMIN)
- [x] Ownership enforcement via SecurityContextHolder
- [ ] JWT refresh tokens
- [ ] Unit and integration tests
- [ ] Docker containerisation
- [ ] Database migrations with Flyway

---

## 👨‍💻 Author

**Olamide** — Backend Developer, building real-world systems one layer at a time.
