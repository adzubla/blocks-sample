package io.adzubla.blocks.sample.order.domain.model;

import io.adzubla.blocks.sample.order.domain.exception.InvalidOrderStateException;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate root of the Order bounded context. All changes to an order go through this class,
 * which enforces the lifecycle in {@link OrderStatus}.
 * <p>
 * Pure Java on purpose: no Spring, no JPA. Persistence maps it to and from its own entities in
 * {@code infrastructure.persistence}.
 */
public class Order {

    private final OrderId id;
    private final String customerEmail;
    private final List<OrderLine> lines;
    private final Instant createdAt;
    private OrderStatus status;
    private String cancellationReason;

    private Order(OrderId id, String customerEmail, List<OrderLine> lines, Instant createdAt,
                  OrderStatus status, String cancellationReason) {
        this.id = Objects.requireNonNull(id, "id");
        this.customerEmail = Objects.requireNonNull(customerEmail, "customerEmail");
        this.lines = List.copyOf(lines);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.status = Objects.requireNonNull(status, "status");
        this.cancellationReason = cancellationReason;
    }

    /** Places a new order. It starts {@link OrderStatus#PENDING} until stock and payment settle. */
    public static Order place(String customerEmail, List<OrderLine> lines) {
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("An order needs at least one line");
        }
        return new Order(OrderId.newId(), customerEmail, lines, Instant.now(), OrderStatus.PENDING, null);
    }

    /** Rebuilds an order that already exists. Used by persistence only; no rules are checked. */
    public static Order restore(OrderId id, String customerEmail, List<OrderLine> lines, Instant createdAt,
                                OrderStatus status, String cancellationReason) {
        return new Order(id, customerEmail, lines, createdAt, status, cancellationReason);
    }

    public void confirm() {
        requirePending("confirm");
        status = OrderStatus.CONFIRMED;
    }

    public void cancel(String reason) {
        requirePending("cancel");
        status = OrderStatus.CANCELLED;
        cancellationReason = reason;
    }

    public Money total() {
        return lines.stream().map(OrderLine::subtotal).reduce(Money.ZERO, Money::plus);
    }

    private void requirePending(String action) {
        // Phase 1: a redelivered event (e.g. a second PaymentCompleted) hits this check and
        // fails. TODO(idempotency): deduplicate deliveries before they reach the aggregate.
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException(id, status, action);
        }
    }

    public OrderId id() {
        return id;
    }

    public String customerEmail() {
        return customerEmail;
    }

    public List<OrderLine> lines() {
        return lines;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public OrderStatus status() {
        return status;
    }

    public String cancellationReason() {
        return cancellationReason;
    }
}
