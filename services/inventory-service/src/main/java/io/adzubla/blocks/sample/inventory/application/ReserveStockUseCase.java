package io.adzubla.blocks.sample.inventory.application;

import io.adzubla.blocks.sample.inventory.application.port.InventoryEventPublisher;
import io.adzubla.blocks.sample.inventory.domain.exception.InsufficientStockException;
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

/** Use case: reserve stock for a newly placed order, and report the outcome. */
@Service
public class ReserveStockUseCase {

    private static final Logger log = LoggerFactory.getLogger(ReserveStockUseCase.class);

    private final ProductRepository products;
    private final ReservationRepository reservations;
    private final InventoryEventPublisher events;

    public ReserveStockUseCase(ProductRepository products, ReservationRepository reservations,
                               InventoryEventPublisher events) {
        this.products = products;
        this.reservations = reservations;
        this.events = events;
    }

    @Transactional
    public void reserve(ReserveStockCommand command) {
        // Phase 1: a redelivered OrderPlaced runs this again and takes the stock a second time.
        // TODO(idempotency): deduplicate the delivery in the listener.
        Map<String, Product> stock = products.findBySkus(command.lines().stream().map(ReservedLine::sku).toList());
        try {
            Reservation reservation = Reservation.reserve(command.orderId(), command.lines(), stock);
            products.saveAll(stock.values());
            reservations.save(reservation);
            log.info("Reserved stock for order {}", command.orderId());
            // TODO(outbox): sent before the commit, so a rollback leaves a false StockReserved.
            events.stockReserved(command.orderId(), command.customerEmail(), command.amount());
        } catch (InsufficientStockException e) {
            log.info("Could not reserve stock for order {}: {}", command.orderId(), e.getMessage());
            events.stockReservationFailed(command.orderId(), e.getMessage());
        }
    }
}
