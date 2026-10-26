# Task API Lab

A small Java 21 / Spring Boot REST CRUD application. We will extend this same repository in later milestones to compare WebSockets, webhooks, gRPC, Kafka and Docker-based local development.

## Prerequisites

- JDK 21
- Maven 3.9+
- Docker Engine / Docker Desktop

Verify:

```bash
java -version
mvn -version
docker --version
docker compose version
```

## Start PostgreSQL

From the repository root:

```bash
docker compose up -d postgres
docker compose ps
```

## Run the application

```bash
mvn clean package
mvn -pl task-service spring-boot:run
```

Application: http://localhost:8080  
Swagger UI: http://localhost:8080/swagger-ui/index.html


## REST endpoints

- `POST /api/tasks` — create a task
- `GET /api/tasks` — list tasks
- `GET /api/tasks/{id}` — get one task
- `PATCH /api/tasks/{id}/status` — update status
- `DELETE /api/tasks/{id}` — delete a task

Example create request:

```bash
curl -i -X POST http://localhost:8080/api/tasks \
  -H 'Content-Type: application/json' \
  -d '{"title":"Learn REST vs gRPC","description":"Build the first lab","priority":"HIGH"}'
```

Example list:

```bash
curl -i http://localhost:8080/api/tasks
```

## Stop PostgreSQL

```bash
docker compose down
```

To delete the local database volume as well (destructive):

```bash
docker compose down -v
```

```bash
 docker compose logs --tail=150 task-service
```

```bash
psql -h localhost -p 15432 -U taskuser -d taskdb 
OR
docker exec -it task-postgres psql -U taskuser -d taskdb

SELECT * FROM tasks;
```
## Learning milestones

1. REST CRUD + PostgreSQL + validation + Flyway
2. WebSocket live task updates
3. Webhook delivery + signature + retry
4. gRPC contract and call
5. Kafka producer/consumer + delivery trade-offs
6. Docker packaging, integration tests and observability
