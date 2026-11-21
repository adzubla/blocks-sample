package io.adzubla.blocks.sample.inventory.infrastructure.persistence;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "reservations")
public class ReservationJpaEntity {

    @Id
    @Column(name = "order_id")
    private UUID orderId;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "reservation_lines", joinColumns = @JoinColumn(name = "order_id"))
    private List<ReservedLineEmbeddable> lines = new ArrayList<>();

    protected ReservationJpaEntity() {
    }

    ReservationJpaEntity(UUID orderId, String status, Instant createdAt, List<ReservedLineEmbeddable> lines) {
        this.orderId = orderId;
        this.status = status;
        this.createdAt = createdAt;
        this.lines = new ArrayList<>(lines);
    }

    UUID getOrderId() {
        return orderId;
    }

    String getStatus() {
        return status;
    }

    Instant getCreatedAt() {
        return createdAt;
    }

    List<ReservedLineEmbeddable> getLines() {
        return lines;
    }
}
