# 💳 Virtual Bank System

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.x-brightgreen)
![MySQL](https://img.shields.io/badge/MySQL-8-blue)
![Kafka](https://img.shields.io/badge/Apache-Kafka-black)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)
![WSO2](https://img.shields.io/badge/WSO2-API_Manager-red)
![Eureka](https://img.shields.io/badge/Spring-Cloud_Eureka-success)

A distributed **Virtual Banking System** developed using **Spring Boot Microservices** following modern enterprise architecture principles.

The system demonstrates service decomposition, asynchronous communication using Kafka, service discovery using Eureka, API management through WSO2 API Manager, and frontend aggregation using the Backend for Frontend (BFF) pattern.

---

# 📖 Project Overview

This project was developed as a **one-month internship project** to demonstrate how a modern banking platform can be implemented using a microservices architecture.

Instead of building one monolithic application, the banking domain is divided into independent services responsible for different business capabilities. These services communicate through REST APIs while Kafka is used for centralized logging.

The system also includes:

- Spring Cloud Eureka for Service Discovery
- WSO2 API Manager as the API Gateway
- Backend For Frontend (BFF)
- Docker Compose deployment
- MySQL databases
- Kafka Logging Service

The objective is to provide hands-on experience in designing, developing, and deploying a secure and scalable distributed banking application.
# 🎯 Project Goals

The Virtual Bank System was designed to demonstrate modern backend development practices including:

- Building independent Spring Boot Microservices
- Applying the Backend for Frontend (BFF) design pattern
- Using WSO2 API Manager as the centralized API Gateway
- Implementing asynchronous communication with Kafka
- Service discovery using Eureka Server
- Deploying services using Docker Compose
- Implementing scheduled jobs
- Maintaining centralized logging
  # ✨ Features

## User Management

- User Registration
- User Login
- Password Hashing
- User Profile Retrieval

## Account Management

- Create Bank Accounts
- Account Balance Management
- Account Retrieval
- User Account Listing
- Scheduled Inactive Account Detection

## Transaction Management

- Transfer Initiation
- Transfer Execution
- Transaction History
- Transaction Status Tracking
- Daily Interest Scheduler

## Backend for Frontend

- Dashboard Aggregation
- Service Orchestration
- Response Transformation

## Logging

- Kafka Producer
- Kafka Consumer
- Centralized Logging Database

## API Management

- OAuth2 Authentication
- API Key Security
- Rate Limiting
- API Products
- Request Routing

## Infrastructure

- Eureka Service Discovery
- Docker Compose Deployment
- MySQL Databases
- Kafka Messaging
  # 🏗️ System Architecture

```
                Client
                   │
                   ▼
        WSO2 API Gateway
                   │
      ┌────────────┴────────────┐
      │                         │
      ▼                         ▼
  BFF Service             Microservices
                                │
     ┌──────────┬──────────┬──────────┬──────────┐
     ▼          ▼          ▼          ▼
 User       Account   Transaction  Logging
 Service     Service     Service    Service
                  │
                  ▼
               Kafka
                  │
                  ▼
          Logging Service

```

### Architecture Components

- **WSO2 API Gateway** acts as the single entry point.
- **BFF Service** aggregates data for frontend applications.
- **User Service** manages users.
- **Account Service** manages bank accounts.
- **Transaction Service** manages transfers.
- **Logging Service** consumes Kafka messages and stores logs.
- **Kafka** transports logging events.
- **Eureka Server** enables service discovery.
- **MySQL** stores persistent data for every service.
  # 🛠️ Technology Stack

| Category | Technologies |
|----------|--------------|
| Language | Java 17 |
| Framework | Spring Boot 4.x |
| Build Tool | Maven |
| Database | MySQL 8 |
| ORM | Spring Data JPA / Hibernate |
| Service Discovery | Spring Cloud Eureka |
| API Gateway | WSO2 API Manager |
| Messaging | Apache Kafka |
| Containerization | Docker & Docker Compose |
| HTTP Client | OpenFeign |
| Scheduler | Spring Scheduler |
| Authentication | Spring Security |
| Password Encryption | BCrypt Password Encoder |
# 📁 Project Structure

```
Virtual_Bank_System
│
├── Eureka_Server/
│
├── User_Service/
│
├── Account_Service/
│
├── Transaction_Service/
│
├── BFF_Service/
│
├── Logging_Service/
│
├── docker-compose.yml
│
└── README.md
```

Each microservice owns:

- Controller Layer
- Service Layer
- Repository Layer
- DTOs
- Entity Models
- Exception Handling
- Configuration
  # 🔍 Microservices Overview

The Virtual Bank System follows a distributed architecture where every service has a single responsibility.

| Service | Responsibility |
|----------|---------------|
| Eureka Server | Service Discovery |
| User Service | User registration and authentication |
| Account Service | Bank account management |
| Transaction Service | Money transfers |
| BFF Service | Aggregates responses for frontend |
| Logging Service | Kafka consumer and centralized logging |
# 👤 User Service

## Responsibilities

The User Service is responsible for managing customer information.

### Features

- Register new users
- Authenticate users
- Retrieve user profile
- Password hashing using BCrypt

### Database

Table:

```
users
```

Main fields include:

- id
- username
- password_hash
- email
- first_name
- last_name

### REST Endpoints

| Method | Endpoint | Description |
|---------|----------|-------------|
| POST | /users/register | Register new user |
| POST | /users/login | User login |
| GET | /users/{id} | Get user profile |

### Business Rules

- Username must be unique.
- Email must be unique.
- Passwords are never stored as plain text.
- Invalid credentials return an error.
  # 🏦 Account Service

## Responsibilities

The Account Service manages all customer bank accounts.

### Features

- Create account
- Retrieve account
- List user accounts
- Deposit
- Withdraw
- Transfer between accounts
- Scheduled inactive account detection

### Database

Table:

```
accounts
```

Main fields:

- account_id
- user_id
- account_number
- account_type
- balance
- status

### REST Endpoints

| Method | Endpoint | Description |
|---------|----------|-------------|
| POST | /accounts | Create account |
| GET | /accounts/{accountId} | Get account |
| GET | /accounts | List accounts |
| PUT | /accounts/deposit | Deposit |
| PUT | /accounts/withdraw | Withdraw |
| PUT | /accounts/transfer | Internal transfer |

### Business Rules

- Balance cannot become negative.
- Transfers require sufficient funds.
- Accounts have ACTIVE and INACTIVE states.
- Scheduled jobs monitor inactive accounts.
  # 🏦 Account Service

## Responsibilities

The Account Service manages all customer bank accounts.

### Features

- Create account
- Retrieve account
- List user accounts
- Deposit
- Withdraw
- Transfer between accounts
- Scheduled inactive account detection

### Database

Table:

```
accounts
```

Main fields:

- account_id
- user_id
- account_number
- account_type
- balance
- status

### REST Endpoints

| Method | Endpoint | Description |
|---------|----------|-------------|
| POST | /accounts | Create account |
| GET | /accounts/{accountId} | Get account |
| GET | /accounts | List accounts |
| PUT | /accounts/deposit | Deposit |
| PUT | /accounts/withdraw | Withdraw |
| PUT | /accounts/transfer | Internal transfer |

### Business Rules

- Balance cannot become negative.
- Transfers require sufficient funds.
- Accounts have ACTIVE and INACTIVE states.
- Scheduled jobs monitor inactive accounts.
  # 💸 Transaction Service

## Responsibilities

The Transaction Service is responsible for managing all money transfer operations between bank accounts.

Unlike the Account Service, it records every transfer, tracks its lifecycle, and provides transaction history.

---

## Features

- Transfer Initiation
- Transfer Execution
- Transaction History
- Transaction Status Tracking
- Daily Interest Scheduler
- Kafka Logging
- Account validation using Account Service
- Transaction persistence

---

## Transaction Lifecycle

```
INITIATED
      │
      ▼
Execute Transfer
      │
      ├──────────────► SUCCESS
      │
      └──────────────► FAILED
```

---

## Database

Table:

```
transactions
```

Main Columns

- transaction_id
- from_account_id
- to_account_id
- amount
- description
- status
- created_at
- updated_at

---

## Transaction Status

| Status | Description |
|---------|-------------|
| INITIATED | Transfer request has been created |
| SUCCESS | Transfer completed successfully |
| FAILED | Transfer failed |

---

## REST Endpoints

| Method | Endpoint | Description |
|---------|----------|-------------|
| POST | /transactions/transfer/initiation | Create transfer request |
| POST | /transactions/transfer/execution | Execute an initiated transfer |
| GET | /accounts/{accountId}/transactions | Retrieve transaction history |

---

## Transfer Flow

### Step 1

Client sends a transfer initiation request.

↓

Transaction Service validates:

- Source account exists
- Destination account exists

↓

Creates transaction with:

```
Status = INITIATED
```

↓

Stores transaction in database.

---

### Step 2

Client requests execution.

↓

Transaction Service calls Account Service.

↓

If transfer succeeds:

```
Status = SUCCESS
```

↓

If transfer fails:

```
Status = FAILED
```

The FAILED status is persisted even if the surrounding transaction rolls back by updating it in a separate transaction.

---

## Transaction History

History returns every transaction where the account is either:

- Sender
- Receiver

Rules:

- Outgoing amounts are returned as negative values.
- Incoming amounts are returned as positive values.
- Delivery status is mapped to:
  - SENT
  - DELIVERED
  - FAILED
    # 📊 Logging Service

## Responsibilities

The Logging Service centralizes logs generated by all microservices.

Instead of each service writing directly to a logging database, services publish log messages to Kafka.

The Logging Service consumes these messages and stores them in its database.

---

## Features

- Kafka Consumer
- Persistent Log Storage
- Request Logging
- Response Logging
- Error Logging

---

## Database

Table:

```
service_logs
```

Fields

- id
- message_type
- message
- date_time

---

## Log Types

### Request

Example

```
Transfer initiation requested.
```

### Response

Example

```
Transaction executed successfully.
```

### Error

Example

```
Insufficient funds.
```
# 📡 Kafka Flow

Kafka is used for asynchronous centralized logging.

```
User Service
        │
        ▼
Kafka Producer
        │
        ▼
Apache Kafka Topic
        │
        ▼
Logging Service
        │
        ▼
MySQL
```

Every service publishes log messages.

Examples include:

- Incoming requests
- Successful responses
- Failed responses
- Validation errors
- Scheduler events

The Logging Service subscribes to the Kafka topic and stores every received message in the `service_logs` table.
# ⏰ Scheduled Jobs

The project includes automated scheduled jobs.

## Daily Interest Scheduler

Runs periodically.

Responsibilities:

- Retrieve active savings accounts.
- Calculate daily interest.
- Create transaction records.
- Execute transfers from the system account.
- Log success or failure.

---

## Inactive Account Scheduler

Runs periodically.

Responsibilities:

- Detect inactive accounts.
- Update account status.
- Record scheduler activity.
  # 🖥️ Backend For Frontend (BFF)

## Responsibilities

The BFF Service acts as a dedicated backend layer for frontend clients.

Instead of calling multiple microservices directly, the frontend communicates only with the BFF.

The BFF coordinates requests across services and returns a single aggregated response.

---

## Responsibilities

- Aggregate data
- Reduce frontend complexity
- Call multiple services
- Build dashboard responses
- Hide internal microservice structure

---

## Example Flow

```
Frontend

↓

BFF Service

↓

User Service

↓

Account Service

↓

Transaction Service

↓

Single Response
```
# 🌐 Eureka Server

Spring Cloud Eureka is used for service discovery.

Each microservice registers itself with Eureka at startup.

Instead of using fixed IP addresses, services communicate using service names.

Example:

```
http://account-service
```

instead of

```
http://localhost:8082
```

Benefits include:

- Dynamic service registration
- Load balancing support
- Easier scalability
- Reduced configuration
  # 🌐 WSO2 API Manager

The Virtual Bank System exposes its APIs through **WSO2 API Manager**, which acts as the central API Gateway.

Instead of clients calling individual microservices directly, all requests pass through WSO2.

---

## Responsibilities

- Publish APIs
- Secure APIs
- Generate OAuth2 Access Tokens
- API Key Authentication
- Rate Limiting
- API Versioning
- API Products
- Request Routing

---

## API Gateway Flow

```
Client
   │
   ▼
WSO2 API Manager
   │
   ▼
Backend For Frontend (BFF)
   │
   ▼
Microservices
```

---

## Published APIs

- User API
- Account API
- Transaction API
- Dashboard API (BFF)

Each API can be tested through the WSO2 Developer Portal after subscribing to an application and generating an access token.
# 🔒 Security

The project applies several security mechanisms.

## Password Security

Passwords are encrypted using **BCrypt Password Encoder** before being stored in the database.

---

## API Authentication

WSO2 API Manager supports:

- OAuth2
- API Keys

Clients must obtain an access token before invoking protected APIs.

---

## Validation

Business validation includes:

- Duplicate usernames
- Duplicate emails
- Invalid credentials
- Invalid account IDs
- Same source and destination account
- Insufficient funds
  # 🐳 Docker Deployment

All services run using Docker Compose.

## Containers

- Eureka Server
- User Service
- User MySQL
- Account Service
- Account MySQL
- Transaction Service
- Transaction MySQL
- Logging Service
- Logging MySQL
- BFF Service
- Kafka
- ZooKeeper
- WSO2 API Manager

---

## Start the Project

```bash
docker compose up --build
```

---

## Stop the Project

```bash
docker compose down
```

---

## Rebuild Images

```bash
docker compose up --build --force-recreate
```
# 🚀 Running the Project

## 1. Clone Repository

```bash
git clone <repository-url>
```

---

## 2. Navigate

```bash
cd Virtual_Bank_System
```

---

## 3. Configure Environment

Update the database passwords and environment variables inside:

```
docker-compose.yml
```

---

## 4. Build

```bash
docker compose up --build
```

---

## 5. Verify Services

Open Eureka Dashboard:

```
http://localhost:8761
```

Verify all services are registered successfully.

---

## 6. Import APIs into WSO2

Import the generated API archives through the Publisher Portal.

Generate an OAuth2 Access Token.

Invoke the published APIs using:

- Postman
- cURL
  # 🧪 Testing

The APIs can be tested using:

- Postman
- cURL
- WSO2 Developer Portal

---

## Example: Register User

```http
POST /users/register
```

---

## Example: Login

```http
POST /users/login
```

---

## Example: Create Account

```http
POST /accounts
```

---

## Example: Transfer Initiation

```http
POST /transactions/transfer/initiation
```

---

## Example: Transfer Execution

```http
POST /transactions/transfer/execution
```

---

## Example: Transaction History

```http
GET /accounts/{accountId}/transactions
```

---

## Example: Dashboard

```http
GET /dashboard/{userId}
```
# 📚 API Summary

| Service | Endpoint |
|----------|----------|
| User | POST /users/register |
| User | POST /users/login |
| User | GET /users/{id} |
| Account | POST /accounts |
| Account | GET /accounts/{accountId} |
| Account | GET /accounts |
| Account | PUT /accounts/deposit |
| Account | PUT /accounts/withdraw |
| Account | PUT /accounts/transfer |
| Transaction | POST /transactions/transfer/initiation |
| Transaction | POST /transactions/transfer/execution |
| Transaction | GET /accounts/{accountId}/transactions |
| BFF | GET /dashboard/{userId} |
# 📈 Future Improvements

Potential future enhancements include:

- JWT Authentication
- Distributed Tracing
- Centralized Configuration Server
- Circuit Breaker and Retry Policies
- Monitoring with Prometheus & Grafana
- ELK Stack Integration
- Kubernetes Deployment
- CI/CD Pipelines with GitHub Actions
- Notification Service
- Email and SMS Alerts
 
