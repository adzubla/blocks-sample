package io.adzubla.blocks.sample.inventory.infrastructure.persistence;

import io.adzubla.blocks.sample.inventory.domain.model.Reservation;
import io.adzubla.blocks.sample.inventory.domain.model.ReservationStatus;
import io.adzubla.blocks.sample.inventory.domain.model.ReservedLine;
import io.adzubla.blocks.sample.inventory.domain.repository.ReservationRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
class JpaReservationRepository implements ReservationRepository {

    private final SpringDataReservationRepository jpa;

    JpaReservationRepository(SpringDataReservationRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Reservation r) {
        // Phase 1: saving a second reservation for the same order (a duplicate OrderPlaced)
        // silently overwrites the first. TODO(idempotency).
        jpa.save(new ReservationJpaEntity(r.orderId(), r.status().name(), r.createdAt(),
                r.lines().stream().map(l -> new ReservedLineEmbeddable(l.sku(), l.quantity())).toList()));
    }

    @Override
    public Optional<Reservation> findByOrderId(UUID orderId) {
        return jpa.findById(orderId).map(e -> Reservation.restore(e.getOrderId(),
                e.getLines().stream().map(l -> new ReservedLine(l.getSku(), l.getQuantity())).toList(),
                e.getCreatedAt(), ReservationStatus.valueOf(e.getStatus())));
    }
}
