# Trail

A multi-tenant incident management backend built on Spring Boot. Incidents move through a governed lifecycle, every state change is recorded in an immutable audit trail (the change and its audit row commit in one transaction), and services communicate over Kafka events rather than direct calls.

Portfolio project, built in the open, one phase at a time.

## Architecture

```
Client (REST)
     |
     v
+---------------------------+        +------------------+
|  Incident Service :8081   |        |   trail-events   |
|  lifecycle + audit trail  |<-------|  shared contract |
|  publishes after commit   |        +------------------+
+---------------------------+
     |                    |
     | JPA                | publish (after commit, keyed by incidentId)
     v                    v
+-----------+     +-------------------------------+
| PostgreSQL|     |  Kafka: incident-events       |
+-----------+     +-------------------------------+
                              |
                              | consume
                              v
                  +-------------------------------+
                  |  Notification Service :8082   |
                  |  @KafkaListener, reacts       |
                  +-------------------------------+
```

The incident lifecycle:

```
OPEN -> ACKNOWLEDGED -> INVESTIGATING -> RESOLVED -> CLOSED
                             ^                |
                             +----- REOPEN ---+
```

Built and running today: the incident service, the notification service, the shared event contract, Postgres, and Kafka. Still to come: DynamoDB dedupe store, WebSocket push, a React and IBM Carbon console, an API gateway, and an AI triage service.

## Tech

- Java 21, Spring Boot 4.1
- PostgreSQL, Flyway for schema migrations
- Apache Kafka (KRaft mode)
- Testcontainers for integration tests against a real Postgres
- Maven multi-module build

## Requirements

- JDK 21
- Maven
- Docker

## Install and run

Start the infrastructure and build:

```bash
cd backend
docker compose up -d postgres kafka
mvn clean install -DskipTests
```

Start the incident service:

```bash
mvn -pl trail-incident-service spring-boot:run
```

In a second terminal, start the notification service:

```bash
mvn -pl trail-notification-service spring-boot:run
```

## Try it

Create an incident (the notification service will react to it):

```bash
curl -s -X POST http://localhost:8081/api/incidents \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: tenant-a" -H "X-Actor: alice" \
  -d '{"title":"payments-api latency","description":"p99 above 4s","source":"prometheus"}'
```

Acknowledge it (use the id from the response):

```bash
curl -s -X POST http://localhost:8081/api/incidents/<id>/transitions \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: tenant-a" -H "X-Actor: alice" \
  -d '{"event":"ACKNOWLEDGE","reason":"on it"}'
```

Read the audit trail:

```bash
curl -s http://localhost:8081/api/incidents/<id>/transitions -H "X-Tenant-Id: tenant-a"
```

Incident service runs on 8081, notification service on 8082.
