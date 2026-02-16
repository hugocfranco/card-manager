# 🛡️ Card Manager API

A high-performance, secure Spring Boot REST API designed to manage credit card information. It features **End-to-End Encryption**, **Bulk Import processing**, and **JWT Authentication**.

## 🚀 Features

- **Security First:**
    - **HTTPS/TLS:** Enforced via self-signed certificate generated automatically during the build.
    - **Blind Index Strategy:** Uses SHA-256 + Salt for searching encrypted data without decrypting it.
    - **AES Encryption:** Sensitive data is encrypted before storage (optional implementation ready).
    - **JWT Authentication:** Stateless authentication with `HmacSHA256`.
- **High Performance:**
    - **Bulk Inserts:** Uses JDBC Template with `rewriteBatchedStatements=true` to handle massive file imports efficiently.
    - **Async Logging:** Integrated with OpenTelemetry for tracing.
- **Dockerized:**
    - Multi-stage Docker build that generates SSL certificates and packages the application.
    - Ready-to-use `docker-compose`.

## 🛠️ Tech Stack

- **Java 21**
- **Spring Boot 4.0** (Web, Security, Data JPA, Actuator)
- **MySQL 8.0**
- **Docker & Docker Compose**
- **Micrometer Tracing** (OpenTelemetry bridge)

---

## ⚙️ Setup & Installation

### Prerequisites
- [Docker](https://www.docker.com/) and Docker Compose installed.

### 1. Clone the repository

### 2. Run the application
```bash
docker-compose up --build
```

### 3. Access the API Documentation
Once the container is running, access the interactive Swagger UI at:
👉 **[https://localhost:8443/swagger-ui.html](https://localhost:8443/swagger-ui.html)**

> **⚠️ SSL Warning:** Since the API generates its own self-signed certificate, your browser will show a security warning. Click **"Advanced"** and then **"Proceed to localhost"** to open the interface.