# Transaction Management System (TMS)

The **Transaction Management System (TMS)** is a backend application designed to handle wallet and transaction management, including automated transaction processing. It provides RESTful APIs for managing wallets and transactions, supports scheduled batch processing of transactions.

### Prerequisites

- JDK 21
- Gradle 8.3
- H2 Database (for development and testing)

### Cloning the Repository

To clone this project, run the following command:

```bash
git clone https://github.com/Fuad-Aliyev/transaction-management-system.git

cd transaction-management-system
```

Running Tests
You can run all tests using the following command:
```bash
./gradlew test
```

Building the Project
To build the project, run:
```bash
./gradlew build
```

Project Package Structure
```bash
src
 ├── main
 │   ├── java/com/khantech/gaming/tms
 │   │   ├── aop          # Aspect-Oriented Programming for logging execution time
 │   │   ├── api          # API builder classes
 │   │   ├── config       # Configuration classes
 │   │   ├── controller   # Wallet and Transaction controllers
 │   │   ├── dto          # Data Transfer Objects
 │   │   ├── exception    # Custom exceptions and handlers
 │   │   ├── mapper       # Entity to DTO mappers
 │   │   ├── model        # JPA Entities
 │   │   ├── repository   # Spring Data JPA repositories
 │   │   ├── scheduler    # Scheduler service for transaction processing
 │   │   ├── service      # Business logic services
 │   │   ├── util         # Utility classes
 │   │   └── validation   # Transaction validation handlers
 │   └── resources
 │       ├── application.properties        # Common settings
 │       ├── application-test.properties   # H2 settings for tests
 │       ├── application-prod.properties   # PostgreSQL settings for production
 │       └── db
 │           ├── h2        # H2 schema and data initialization scripts
 │           └── postgres  # PostgreSQL schema and data initialization scripts
 └── test
     ├── java/com/khantech/gaming/tms
     │   ├── integration  # Integration tests
     │   └── unit         # Unit tests
     └── resources
         ├── application-test.properties  # H2 settings for tests
         ├── schema.sql                   # Schema for H2
         └── data.sql                     # Initial data for H2
```

API Documentation

Wallet Controller

	•	Create Wallet: POST /api/v1/wallets
	•	Get Wallets for User: GET /api/v1/wallets/{userId}

Transaction Controller

	•	Create Transaction: POST /api/v1/transactions
	•	Approve Transaction: POST /api/v1/transactions/{transactionId}/approve

 Scheduler

The scheduler is responsible for processing pending transactions every 24 hours. It runs based on the cron expression configured in the application.properties:
This expression can be adjusted for different scheduling needs.
```bash
scheduling.cron.process-transactions=0 0 0 * * *
```

Testing

The project includes both unit and integration tests:

	•	Unit Tests: Written for the core services (TransactionApproval, TransactionBatchProcessor, TransactionCreation, WalletService).
	•	Integration Tests: Cover the REST APIs for wallets and transactions, ensuring that the entire stack works as expected.

To run the tests:
```bash
./gradlew test
```

Configuration

Profiles

There are 2 profiles. test and prod. test is used for h2 and prod can be used for Postgres. Testing uses test profile.

	•	Development: Uses H2 in-memory database and profile is set to test.
	•	Production: Can be configured to use PostgreSQL and profile is set to prod.
	•	Test: Uses H2 for running tests and profile is set to test.

Database Initialization

H2 Database (Development & Testing)

The H2 database schema and initial data are located under the resources/db/h2 folder. These scripts are automatically applied during application startup:

	•	schema-h2.sql
	•	data-h2.sql

Technologies

	•	Java 21: Main programming language
	•	Spring Boot: Framework used for developing the REST APIs and services
	•	Spring Data JPA: For data persistence and interaction with databases
	•	Hibernate: ORM framework
	•	H2 Database: Used for development and testing
	•	JUnit 5: For unit and integration testing
	•	MockMvc: For API testing
	•	Gradle: Build automation tool

Contributing

Feel free to submit pull requests or open issues on the repository. All contributions are welcome.
