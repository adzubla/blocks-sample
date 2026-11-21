package io.adzubla.blocks.sample.order.application;

import io.adzubla.blocks.sample.order.application.port.OrderEventPublisher;
import io.adzubla.blocks.sample.order.application.port.ProductCatalog;
import io.adzubla.blocks.sample.order.domain.model.Money;
import io.adzubla.blocks.sample.order.domain.model.Order;
import io.adzubla.blocks.sample.order.domain.model.OrderLine;
import io.adzubla.blocks.sample.order.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Use case: a customer places an order.
 * <ol>
 *   <li>Prices the lines with a synchronous call to the product catalog (inventory-service).</li>
 *   <li>Saves the order as PENDING.</li>
 *   <li>Publishes {@code OrderPlaced}. Inventory reacts to it, and the rest of the flow follows
 *       by choreography.</li>
 * </ol>
 */
@Service
public class PlaceOrderUseCase {

    private final ProductCatalog productCatalog;
    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    public PlaceOrderUseCase(ProductCatalog productCatalog, OrderRepository orderRepository,
                             OrderEventPublisher eventPublisher) {
        this.productCatalog = productCatalog;
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Order place(PlaceOrderCommand command) {
        Set<String> skus = command.items().stream().map(PlaceOrderCommand.Item::sku).collect(Collectors.toSet());
        Map<String, Money> prices = productCatalog.findPrices(skus);

        Set<String> unknown = new HashSet<>(skus);
        unknown.removeAll(prices.keySet());
        if (!unknown.isEmpty()) {
            throw new UnknownProductException(unknown);
        }

        List<OrderLine> lines = command.items().stream()
                .map(item -> new OrderLine(item.sku(), item.quantity(), prices.get(item.sku())))
                .toList();
        Order order = Order.place(command.customerEmail(), lines);
        orderRepository.save(order);

        // Dual write: the event is sent to Kafka inside the DB transaction. If the commit then
        // fails, the event announces an order that doesn't exist. If Kafka is unreachable, the
        // order is committed but nobody hears about it and it stays PENDING forever.
        // TODO(outbox): store the event in an outbox table in this same transaction instead.
        eventPublisher.orderPlaced(order);
        return order;
    }
}
