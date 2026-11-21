package io.adzubla.blocks.sample.inventory.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataProductRepository extends JpaRepository<ProductJpaEntity, String> {
}
