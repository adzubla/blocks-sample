package io.adzubla.blocks.sample.inventory.domain.repository;

import io.adzubla.blocks.sample.inventory.domain.model.Reservation;

import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository {

    void save(Reservation reservation);

    Optional<Reservation> findByOrderId(UUID orderId);
}
