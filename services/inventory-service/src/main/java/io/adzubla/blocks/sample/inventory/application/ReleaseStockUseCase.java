package io.adzubla.blocks.sample.inventory.application;

import io.adzubla.blocks.sample.inventory.domain.model.Product;
import io.adzubla.blocks.sample.inventory.domain.model.Reservation;
import io.adzubla.blocks.sample.inventory.domain.model.ReservedLine;
import io.adzubla.blocks.sample.inventory.domain.repository.ProductRepository;
import io.adzubla.blocks.sample.inventory.domain.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

/**
 * Use case: give back the stock of an order whose payment failed. This is the compensating
 * action of the choreographed flow.
 * <p>
 * TODO(saga): with orchestration, this runs on a ReleaseStock command from the saga.
 */
@Service
public class ReleaseStockUseCase {

    private static final Logger log = LoggerFactory.getLogger(ReleaseStockUseCase.class);

    private final ProductRepository products;
    private final ReservationRepository reservations;

    public ReleaseStockUseCase(ProductRepository products, ReservationRepository reservations) {
        this.products = products;
        this.reservations = reservations;
    }

    @Transactional
    public void release(UUID orderId) {
        Reservation reservation = reservations.findByOrderId(orderId).orElse(null);
        if (reservation == null) {
            log.warn("No reservation to release for order {}", orderId);
            return;
        }
        Map<String, Product> stock = products.findBySkus(reservation.lines().stream().map(ReservedLine::sku).toList());
        reservation.release(stock);
        products.saveAll(stock.values());
        reservations.save(reservation);
        log.info("Released stock for order {}", orderId);
    }
}
