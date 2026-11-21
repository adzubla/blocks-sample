# blocks-sample

A small e-commerce system built **for teaching**. It shows how to structure a set of
microservices and how they talk to each other. The architecture is the subject. The
e-commerce features are only there to give the architecture something to do.

## Goals

- Show a **microservices architecture** with clear service boundaries: Order, Inventory,
  Payment and Notification.
- Show a **Domain-Driven Design** code layout inside each service (`domain`, `application`,
  `infrastructure`, `interfaces`).
- Show the **three interaction styles** and when to use each one:
  - **REST** for synchronous queries,
  - **Kafka** for domain events (facts that already happened),
  - **RabbitMQ** for commands and work queues.
- Run the whole system locally with **Docker Compose**, with UIs that let you watch it
  work: Kafka UI, RabbitMQ management, Mailpit and Grafana.

## Non-goals

- Realistic business rules. Pricing, stock and payments are as simple as they can be.
- Production hardening. External systems such as the payment gateway and the email provider
  are dummies.

## Tech stack

Java 17 · Spring Boot 4.0.10 · Maven (multi-module) · PostgreSQL · Kafka · RabbitMQ ·
Grafana (OpenTelemetry LGTM stack) · Docker Compose

## Phased evolution

The project is built in phases. Each phase fixes the flaws that the previous one left in
on purpose. The overall structure is settled in Phase 1, so later phases add capabilities
without reorganising the code.

| Phase | Focus |
|-------|-------|
| 1 | Naive baseline: services, DDD layout, REST + Kafka + RabbitMQ, choreography. See [PHASE_1.md](PHASE_1.md). |
| later | Idempotency & deduplication ([blocks-idempotency](README-idempotency.md)) |
| later | Outbox pattern (reliable event publishing) |
| later | Saga orchestration pattern |
| later | Unified error handler for REST APIs ([blocks-error-handler](README-error-handler.md)) |

The code marks the places where these capabilities will go with comments:
`TODO(idempotency)`, `TODO(outbox)`, `TODO(saga)` and `TODO(error-handler)`.

## Quick start

```bash
docker compose up --build -d
curl -s -X POST localhost:8081/orders -H 'Content-Type: application/json' \
  -d '{"customerEmail":"ana@example.com","items":[{"sku":"KEYBOARD","quantity":1}]}'
```

See [PHASE_1.md § Running the demo](PHASE_1.md#7-running-the-demo) for the scenarios and UIs.

## Repository layout

| Path | What |
|------|------|
| `contracts/` | Events, commands, topic/queue names: the only code services share |
| `services/*` | order, inventory, payment and notification services (DDD layout) |
| `external/fake-payment-gateway` | Dummy external payment provider |
| `docker/`, `docker-compose.yml` | Container build and the local environment |
| `http/requests.http` | Demo requests |
