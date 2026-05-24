# 🏦 Banking App

A production-grade Banking REST API built with Java 21 and Spring Boot 3.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![Kafka](https://img.shields.io/badge/Apache%20Kafka-7.5-black)
![License](https://img.shields.io/badge/License-MIT-yellow)

## 📋 Features

- **JWT Authentication** — Register, login, token refresh with role-based access control
- **Account Management** — Open current/savings accounts with IBAN generation
- **Transactions** — Transfer funds between accounts with full history and pagination
- **Loan Module** — Apply for loans with automatic amortization schedule calculation
- **Kafka Notifications** — Event-driven email notifications after each transaction
- **Audit Logging** — Automatic audit trail using Spring AOP
- **Optimistic Locking** — Concurrent transaction safety
- **API Documentation** — Interactive Swagger UI

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| Security | Spring Security + JWT |
| Database | PostgreSQL 16 |
| Migration | Liquibase |
| Messaging | Apache Kafka |
| Testing | JUnit 5, Mockito, TestContainers |
| Documentation | Swagger / OpenAPI 3 |
| Build | Gradle |
| Containerization | Docker + Docker Compose |
| CI/CD | GitHub Actions |

## 🏗️ Architecture

```
├── controller      # REST endpoints
├── service         # Business logic
├── repository      # Data access layer
├── entity          # JPA entities
├── dto             # Data Transfer Objects
├── security        # JWT filter, JwtService
├── config          # Spring Security, OpenAPI config
├── exception       # Global exception handler
├── aspect          # AOP audit logging
└── annotation      # Custom annotations
```

## 🚀 Getting Started

### Prerequisites
- Java 21
- Docker & Docker Compose

### Run with Docker

```bash
# Clone the repository
git clone https://github.com/ffatullayev01/banking-app.git
cd banking-app

# Start PostgreSQL and Kafka
docker-compose up -d

# Run the application
./gradlew bootRun
```

### Access
- **API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui/index.html

## 📡 API Endpoints

### Authentication
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/auth/register` | Register new user |
| POST | `/api/v1/auth/login` | Login and get JWT token |
| POST | `/api/v1/auth/refresh-token` | Refresh access token |

### Accounts
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/accounts` | Open new account |
| GET | `/api/v1/accounts` | Get my accounts |
| GET | `/api/v1/accounts/{iban}` | Get account by IBAN |

### Transactions
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/transactions/transfer` | Transfer funds |
| GET | `/api/v1/transactions/history/{iban}` | Transaction history |

### Loans
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/loans/apply` | Apply for loan |
| GET | `/api/v1/loans` | Get my loans |
| GET | `/api/v1/loans/{id}/schedule` | Get payment schedule |

## 🧪 Running Tests

```bash
# Run all tests
./gradlew test

# Run only unit tests
./gradlew test --tests "*.service.*"

# Run only integration tests
./gradlew test --tests "*.integration.*"
```

## 🔐 Security

- Passwords are hashed using BCrypt
- JWT tokens expire after 24 hours
- Refresh tokens expire after 7 days
- All endpoints except `/api/v1/auth/**` require authentication
- Role-based access control: `ADMIN`, `CUSTOMER`, `EMPLOYEE`

## 📊 Database Schema

```
users
  └── accounts (one-to-many)
        └── transactions (many-to-many via sender/receiver)
  └── loans (one-to-many)
  └── audit_logs (one-to-many)
```

## 👤 Author

**Farid Fətullayev**
- GitHub: [@ffatullayev01](https://github.com/ffatullayev01)