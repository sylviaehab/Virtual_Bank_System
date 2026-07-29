# 💳 Virtual Bank System

The **Virtual Bank System** is a microservices-based banking application designed to provide secure and scalable banking operations. The project demonstrates how multiple independent services can work together using Spring Boot, Spring Cloud, Apache Kafka, Docker, and WSO2 API Manager.

The system follows a distributed architecture where each microservice is responsible for a specific business capability while communicating with other services through REST APIs.

---

# 📖 Project Overview

The project consists of several independent microservices that collaborate to perform banking operations.

The main objectives of the system are:

- Manage users.
- Manage bank accounts.
- Process money transfers.
- Aggregate data through a Backend for Frontend (BFF).
- Centralize logging using Apache Kafka.
- Publish and secure APIs using WSO2 API Manager.
- Register services using Eureka Server.

---

# 🏗️ System Architecture

The system consists of the following components:

- Eureka Server
- User Service
- Account Service
- Transaction Service
- Backend For Frontend (BFF) Service
- Logging Service
- Apache Kafka
- WSO2 API Manager
- MySQL Databases

Each microservice owns its own database and communicates with other services using REST APIs.

Apache Kafka is used to transfer log messages asynchronously to the Logging Service.

WSO2 API Manager acts as the API Gateway for publishing and securing APIs.

---

# ⚙️ Technology Stack

## Backend

- Java
- Spring Boot
- Spring Data JPA
- Spring Cloud
- Spring Security
- OpenFeign

## Database

- MySQL

## Messaging

- Apache Kafka

## Service Discovery

- Eureka Server

## API Gateway

- WSO2 API Manager

## Containerization

- Docker
- Docker Compose

---

# 📁 Project Structure

```
Virtual_Bank_System
│
├── Eureka_Server
├── User_Service
├── Account_Service
├── Transaction_Service
├── BFF_Service
├── Logging_Service
├── docker-compose.yml
└── README.md
```

Each service is implemented independently and is responsible for a specific business domain.

---

# 🔍 Microservices Overview

## User Service

Responsible for managing user information and authentication.

## Account Service

Responsible for creating and managing bank accounts.

## Transaction Service

Responsible for processing transfers and maintaining transaction history.

## Backend For Frontend (BFF)

Aggregates responses from multiple services for frontend clients.

## Logging Service

Consumes Kafka messages and stores service logs.

## Eureka Server

Registers all microservices and enables service discovery.

## WSO2 API Manager

Publishes, secures, and manages APIs exposed by the system.
# 👤 User Service

## Overview

The User Service is responsible for managing user information and authentication.

It provides functionality for:

- User registration
- User login
- Retrieving user profile information

---

## Responsibilities

- Register new users
- Authenticate existing users
- Retrieve user profile information

---

## API Endpoints

| Method | Endpoint | Description |
|---------|----------|-------------|
| POST | `/users/register` | Register a new user |
| POST | `/users/login` | Authenticate a user |
| GET | `/users/{userId}` | Retrieve user profile |

---

## Registration

Creates a new user after validating the request.

The service ensures that duplicate users are not created.

---

## Login

Authenticates a registered user using the provided credentials.

---

## User Profile

Returns the information associated with a specific user.

---

# 🏦 Account Service

## Overview

The Account Service is responsible for managing customer bank accounts.

It provides functionality for account creation, account retrieval, money transfers, and scheduled account monitoring.

---

## Responsibilities

- Create bank accounts
- Retrieve account details
- Retrieve accounts belonging to a user
- Transfer funds between accounts
- Monitor inactive accounts

---

## API Endpoints

| Method | Endpoint | Description |
|---------|----------|-------------|
| POST | `/accounts` | Create a bank account |
| GET | `/accounts/{accountId}` | Retrieve account details |
| GET | `/accounts/users/{userId}` | Retrieve all accounts for a user |
| PUT | `/accounts/transfer` | Transfer funds between accounts |

---

## Create Account

Creates a new bank account for an existing user.

---

## Retrieve Account

Returns the details of a specific account.

---

## Retrieve User Accounts

Returns all accounts that belong to a particular user.

---

## Transfer Funds

Transfers money between two accounts after validating the request.

---

## Scheduled Task

The Account Service includes a scheduled process that monitors inactive accounts according to the project requirements.
# 💸 Transaction Service

## Overview

The Transaction Service is responsible for processing money transfers between accounts and maintaining transaction records.

It manages the transfer lifecycle from initiation to execution and provides transaction history.

---

## Responsibilities

- Initiate money transfers
- Execute initiated transfers
- Store transaction records
- Retrieve transaction history
- Process scheduled interest credits

---

## API Endpoints

| Method | Endpoint | Description |
|---------|----------|-------------|
| POST | `/transactions/transfer/initiation` | Initiate a money transfer |
| POST | `/transactions/transfer/execution` | Execute an initiated transfer |
| GET | `/accounts/{accountId}/transactions` | Retrieve transaction history for an account |

---

## Transfer Initiation

Creates a new transaction with an initial status before the transfer is executed.

---

## Transfer Execution

Executes a previously initiated transaction by communicating with the Account Service.

The transaction status is updated according to the execution result.

---

## Transaction History

Returns the transaction history associated with a specific account.

---

## Daily Interest Scheduler

The Transaction Service includes a scheduled task that calculates and credits daily interest to eligible accounts.

The scheduler retrieves the required accounts, calculates the interest amount, performs the transfer, and records the corresponding transaction.

---

# 📊 Logging Service

## Overview

The Logging Service centralizes application logs generated by all microservices.

Instead of each service storing logs independently, log messages are published to Apache Kafka and consumed by the Logging Service.

---

## Responsibilities

- Consume Kafka messages
- Store service logs
- Record request messages
- Record response messages

---

## Logging Flow

Each microservice publishes log messages to Apache Kafka.

The Logging Service subscribes to the configured Kafka topic and persists the received logs into its database.

---

# 📡 Apache Kafka

Apache Kafka is used as the messaging platform for centralized logging.

All participating services publish request and response log messages.

The Logging Service acts as the Kafka consumer and stores the received messages.

---

## Kafka Producer

Implemented by the application services to publish log messages.

---

## Kafka Consumer

Implemented by the Logging Service to consume and persist log messages.

---

## Message Types

The document describes two message types:

- Request
- Response

---

# ⏰ Scheduled Jobs

The project includes scheduled background tasks.

## Account Service

The Account Service includes a scheduled process for monitoring inactive accounts.

---

## Transaction Service

The Transaction Service includes a scheduled process responsible for calculating and crediting daily interest.
# 🌐 WSO2 API Manager

## Overview

The Virtual Bank System uses **WSO2 API Manager** as the API Gateway.

WSO2 provides a centralized entry point for the system APIs and offers API publishing, subscription management, authentication, and monitoring capabilities.

---

## Responsibilities

- Publish APIs
- Manage API subscriptions
- Secure APIs
- Route client requests
- Manage API lifecycle

---

## API Publishing

The project APIs are published through the WSO2 Publisher Portal.

Once published, APIs become available for subscription through the Developer Portal.

---

## API Subscription

Applications subscribe to the published APIs before invoking them.

After subscription, an access token can be generated for authenticated requests.

---

## API Product

The project also demonstrates the use of an API Product, allowing multiple APIs to be grouped and exposed as a single product.

---

# 🔐 Security

The project uses WSO2 API Manager to secure exposed APIs.

Authentication is performed using OAuth2 access tokens generated after subscribing to an application.

---

# 🐳 Docker Deployment

The application is deployed using Docker Compose.

Each microservice runs inside its own Docker container together with the required infrastructure components.

---

## Infrastructure Components

The deployment includes:

- Eureka Server
- User Service
- Account Service
- Transaction Service
- Backend for Frontend (BFF)
- Logging Service
- Apache Kafka
- ZooKeeper
- MySQL databases
- WSO2 API Manager

---

# 🚀 Running the Project

## Build and Start

Build and start all services using Docker Compose.

After the containers start successfully:

- Verify that Eureka Server is running.
- Verify that all services are registered.
- Access WSO2 API Manager.
- Publish and subscribe to the required APIs.
- Generate an access token.
- Invoke the APIs through WSO2.

---

# 🔄 System Workflow

The overall system workflow is as follows:

1. The client sends a request.
2. The request reaches WSO2 API Manager.
3. WSO2 forwards the request to the appropriate backend service.
4. Services communicate with one another when required.
5. Log messages are published to Apache Kafka.
6. Logging Service consumes the messages and stores them.
7. The response is returned to the client.

---

# 📚 Project Components Summary

| Component | Responsibility |
|----------|----------------|
| Eureka Server | Service Discovery |
| User Service | User management and authentication |
| Account Service | Account management |
| Transaction Service | Money transfer processing |
| Backend for Frontend | Response aggregation |
| Logging Service | Centralized logging |
| Apache Kafka | Message broker for logging |
| WSO2 API Manager | API Gateway |

---

# 📌 Conclusion

The Virtual Bank System demonstrates the implementation of a microservices-based banking application using Spring Boot and Spring Cloud.

The project integrates service discovery, centralized logging through Apache Kafka, API management using WSO2 API Manager, and containerized deployment with Docker Compose to provide a modular and scalable architecture.
# 🐳 Docker Deployment

The Virtual Bank System is containerized using **Docker Compose**. Each microservice and infrastructure component runs in its own container.

---

## Infrastructure Containers

| Container | Description | Port |
|----------|-------------|------|
| eureka-server | Service Discovery | 8761 |
| user-service | User Service | 8081 |
| user-service-mysql | User Service Database | 3307 |
| account-service | Account Service | 8082 |
| account-service-mysql | Account Service Database | 3308 |
| transaction-service | Transaction Service | 8083 |
| transaction-service-mysql | Transaction Service Database | 3309 |
| bff-service | Backend For Frontend | 8084 |
| logging-service | Logging Service | 8085 |
| logging-service-mysql | Logging Database | 3310 |
| kafka | Apache Kafka | 9092 |
| zookeeper | Apache ZooKeeper | 2181 |
| wso2am | WSO2 API Manager | 9443, 8243 |

---

# 🚀 Running the Project

## Clone the Repository

```bash
git clone <repository-url>
cd Virtual_Bank_System
```

---

## Build All Services

```bash
docker compose build
```

---

## Start All Containers

```bash
docker compose up -d
```

Or build and start together:

```bash
docker compose up --build -d
```

---

## View Running Containers

```bash
docker ps
```

---

## View Logs

Transaction Service

```bash
docker logs -f transaction-service
```

Account Service

```bash
docker logs -f account-service
```

User Service

```bash
docker logs -f user-service
```

Logging Service

```bash
docker logs -f logging-service
```

Kafka

```bash
docker logs -f kafka
```

---

## Stop All Containers

```bash
docker compose down
```

---

## Rebuild Containers

```bash
docker compose down
docker compose up --build
```

---

# 🌐 Service URLs

| Service | URL |
|---------|-----|
| Eureka Dashboard | http://localhost:8761 |
| User Service | http://localhost:8081 |
| Account Service | http://localhost:8082 |
| Transaction Service | http://localhost:8083 |
| BFF Service | http://localhost:8084 |
| Logging Service | http://localhost:8085 |
| WSO2 Publisher | https://localhost:9443/publisher |
| WSO2 Developer Portal | https://localhost:9443/devportal |
| WSO2 Gateway | https://localhost:8243 |

---

# 🧪 Testing APIs

## Register User

```http
POST http://localhost:8081/users/register
```

---

## Login

```http
POST http://localhost:8081/users/login
```

---

## Get User Profile

```http
GET http://localhost:8081/users/{userId}
```

---

## Create Account

```http
POST http://localhost:8082/accounts
```

---

## Get Account

```http
GET http://localhost:8082/accounts/{accountId}
```

---

## Get User Accounts

```http
GET http://localhost:8082/accounts/users/{userId}
```

---

## Transfer Funds

```http
PUT http://localhost:8082/accounts/transfer
```

---

## Initiate Transfer

```http
POST http://localhost:8083/transactions/transfer/initiation
```

---

## Execute Transfer

```http
POST http://localhost:8083/transactions/transfer/execution
```

---

## Transaction History

```http
GET http://localhost:8083/accounts/{accountId}/transactions
```

---

# 📡 WSO2 Testing

After publishing the APIs in WSO2:

1. Open **Publisher**
   ```
   https://localhost:9443/publisher
   ```

2. Publish the API.

3. Open **Developer Portal**
   ```
   https://localhost:9443/devportal
   ```

4. Subscribe to the API.

5. Generate an OAuth2 access token.

6. Invoke the API through the Gateway.

Example:

```http
POST https://localhost:8243/vbank/1.0.0/transactions/transfer/initiation
```

or

```http
POST https://localhost:8243/vbank/1.0.0/transactions/transfer/execution
```
