package io.adzubla.blocks.sample.order.infrastructure.persistence;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Persistence model of an order. Kept apart from the domain {@code Order} so the domain stays
 * free of JPA. {@link JpaOrderRepository} maps between the two.
 */
@Entity
@Table(name = "orders")
public class OrderJpaEntity {

    @Id
    private UUID id;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private String status;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_lines", joinColumns = @JoinColumn(name = "order_id"))
    @OrderColumn(name = "line_no")
    private List<OrderLineEmbeddable> lines = new ArrayList<>();

    protected OrderJpaEntity() {
    }

    OrderJpaEntity(UUID id, String customerEmail, String status, String cancellationReason, Instant createdAt,
                   List<OrderLineEmbeddable> lines) {
        this.id = id;
        this.customerEmail = customerEmail;
        this.status = status;
        this.cancellationReason = cancellationReason;
        this.createdAt = createdAt;
        this.lines = new ArrayList<>(lines);
    }

    UUID getId() {
        return id;
    }

    String getCustomerEmail() {
        return customerEmail;
    }

    String getStatus() {
        return status;
    }

    String getCancellationReason() {
        return cancellationReason;
    }

    Instant getCreatedAt() {
        return createdAt;
    }

    List<OrderLineEmbeddable> getLines() {
        return lines;
    }
}
