Virtual Bank System
A simplified virtual banking system built as Java Spring Boot microservices, fronted by a BFF and a WSO2 API Gateway.

Full spec and endpoint reference: Virtual_Bank_System_Documentation.md (or the Word version, Virtual_Bank_System_Documentation.docx).

1. Architecture
Service	Container name	Port	DB
Eureka Server	eureka-server	8761	—
User Service	user-service	8081	user-service-mysql (3307)
Account Service	account-service	8082	account-service-mysql (3308)
Transaction Service	transaction-service	8083	transaction-service-mysql (3309)
BFF Service	bff-service	8084	— (aggregates User/Account/Transaction)
Logging Service	logging-service	8085	logging-service-mysql (3310)
Zookeeper	zookeeper	2181	—
Kafka	kafka	9092	—
WSO2 API Manager	wso2am	9443 (Publisher/Dev Portal), 8243 (Gateway)	—
User, Account, Transaction, BFF, and Logging Services all connect to the same shared Kafka topic, virtual-bank-logs, for centralized logging: each publishes request/response/event messages to it, and Logging Service consumes from it and persists to its own DB (the "dump table" from the spec).

2. Prerequisites
Docker + Docker Compose
Java 11 and Maven (only needed if building/running a service outside Docker)
Postman (for manual API testing)
3. Setup
3.1. Create a .env file in the project root (same directory as docker-compose.yml):

bash
MYSQL_ROOT_PASSWORD=<choose a root password>
USER_DB_PASSWORD=<choose a password>
ACCOUNT_DB_PASSWORD=<choose a password>
TRANSACTION_DB_PASSWORD=<choose a password>
LOGGING_DB_PASSWORD=<choose a password>
3.2. Bring everything up:

bash
docker compose up -d --build
--build matters after any source code change — otherwise Docker reuses the old image.

3.3. Confirm everything started:

bash
docker ps
You should see all of: eureka-server, user-service, user-service-mysql, account-service, account-service-mysql, transaction-service, transaction-service-mysql, bff-service, logging-service, logging-service-mysql, zookeeper, kafka, wso2am. MySQL containers should show (healthy).

3.4. Check individual service logs if something looks off:

bash
docker logs --tail 50 <container-name>
4. WSO2 API Gateway Setup (One-Time)
4.1. Open the Publisher Portal: https://localhost:9443/publisher (admin / admin, accept the self-signed certificate warning).

4.2. Create the 4 APIs and the vbank API Product per the table in the full documentation, §5. Backend endpoints use container names (e.g. http://transaction-service:8083, http://bff-service:8084), since WSO2 runs in the same Docker network.

4.3. Enable both OAuth2 and Api Key under each API/Product's Runtime Configurations → Application Level Security.

4.4. Attach an Add Header policy (APP-NAME: MOBILE or PORTAL) to each operation's request flow.

4.5. Deploy a revision and Publish.

4.6. In the Developer Portal (https://localhost:9443/devportal), create the vbank portal and vbank mobile applications, subscribe them to vbank, and generate OAuth2 keys/access tokens and API keys for testing.

Don't skip this: wso2am is configured with a persistent volume (wso2am-data) in docker-compose.yml, so this setup only needs to be done once — it survives docker compose down/up. If that volume is ever removed, this section needs to be redone.

5. Testing with Postman
5.1. Example — initiate a transfer (see full request/response shapes in the documentation):

bash
POST https://localhost:8243/vbank/1.0.0/initiation
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "fromAccountId": "<real account UUID>",
  "toAccountId": "<real account UUID>",
  "amount": 30.00,
  "description": "Transfer to checking account"
}
Use real account UUIDs created via POST /accounts — the service validates UUID format and account existence.

5.2. Watch Kafka logging live:

bash
docker exec -it kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic virtual-bank-logs --from-beginning
You should see events published by every service (TRANSACTION_INITIATED, TRANSACTION_EXECUTED, EXECUTION_FAILED, INTEREST_SCHEDULER_RESULT from Transaction Service, plus Request/Response messages from User, Account, and BFF) — never passwords, tokens, credentials, or free-text personal data.

5.3. Confirm Logging Service persisted them:

bash
docker exec -it logging-service-mysql mysql -u logging_service_app -p logging_service_db -e "SELECT * FROM <dump_table_name> ORDER BY id DESC LIMIT 10;"
(replace <dump_table_name> with whatever the Logging Service actually names its dump table)

6. Troubleshooting
Symptom	Likely Cause / Fix
kafka container stuck in Created, never starts	Zookeeper wasn't ready when Kafka's readiness check ran. Retry docker compose down && docker compose up -d, or check docker logs zookeeper / docker logs kafka.
404 Not Found ("type": "Status report") from the Gateway	Resource path doesn't match what's configured in WSO2, or the API/Product was never (re)deployed after an edit. Check the resource's URL pattern and redeploy.
401 / 900902 Missing Credentials	Authorization header missing, unchecked in Postman, or missing the Bearer  prefix before the token.
500 with a raw stack trace instead of a clean error	Check the relevant service's logs (e.g. docker logs transaction-service) — often a downstream call (Account Service, BFF, or Kafka) failing. Confirm the downstream container is Up/healthy.
No messages appear on virtual-bank-logs	Confirm kafka shows Up, and that the producing service's SPRING_KAFKA_BOOTSTRAP_SERVERS / APP_KAFKA_LOG_TOPIC (or TRANSACTION_KAFKA_TOPIC for Transaction Service) env vars match kafka:9092 and virtual-bank-logs.
Logging Service isn't persisting messages	Check docker logs logging-service and confirm logging-service-mysql shows (healthy) before logging-service started.
WSO2 Publisher/Dev Portal is suddenly empty after a restart	The wso2am-data volume wasn't mounted before that restart. Confirm the volume is present in docker-compose.yml and re-create the APIs/Product/apps — this only needs to happen once going forward.
7. Repository Layout
/Eureka_Server/Eureka_Server     → Eureka Server source + Dockerfile
/User_Service                    → User Service source + Dockerfile
/Account_Service                 → Account Service source + Dockerfile
/Transaction_Service              → Transaction Service source + Dockerfile
/BFF_Service                      → BFF Service source + Dockerfile
/Logging_Service                  → Logging Service source + Dockerfile
docker-compose.yml                → full local stack
.env                              → DB passwords (not committed — see §3.1)
Virtual_Bank_System_Documentation.md    → full spec + endpoint reference (Markdown)
Virtual_Bank_System_Documentation.docx  → full spec + endpoint reference (Word)
README.md                         → this file
