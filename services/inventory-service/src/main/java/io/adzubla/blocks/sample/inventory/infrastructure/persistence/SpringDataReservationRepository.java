package io.adzubla.blocks.sample.inventory.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface SpringDataReservationRepository extends JpaRepository<ReservationJpaEntity, UUID> {
}
