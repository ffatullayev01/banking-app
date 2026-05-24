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