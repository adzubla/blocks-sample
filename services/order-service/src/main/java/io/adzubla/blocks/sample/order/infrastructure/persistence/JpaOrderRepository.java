package io.adzubla.blocks.sample.order.infrastructure.persistence;

import io.adzubla.blocks.sample.order.domain.model.Money;
import io.adzubla.blocks.sample.order.domain.model.Order;
import io.adzubla.blocks.sample.order.domain.model.OrderId;
import io.adzubla.blocks.sample.order.domain.model.OrderLine;
import io.adzubla.blocks.sample.order.domain.model.OrderStatus;
import io.adzubla.blocks.sample.order.domain.repository.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Adapter: implements the domain's {@link OrderRepository} with Spring Data JPA. */
@Repository
class JpaOrderRepository implements OrderRepository {

    private final SpringDataOrderRepository jpa;

    JpaOrderRepository(SpringDataOrderRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Order order) {
        jpa.save(toEntity(order));
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return jpa.findById(id.value()).map(JpaOrderRepository::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return jpa.findTop50ByOrderByCreatedAtDesc().stream().map(JpaOrderRepository::toDomain).toList();
    }

    private static OrderJpaEntity toEntity(Order order) {
        List<OrderLineEmbeddable> lines = order.lines().stream()
                .map(l -> new OrderLineEmbeddable(l.sku(), l.quantity(), l.unitPrice().amount()))
                .toList();
        return new OrderJpaEntity(order.id().value(), order.customerEmail(), order.status().name(),
                order.cancellationReason(), order.createdAt(), lines);
    }

    private static Order toDomain(OrderJpaEntity e) {
        List<OrderLine> lines = e.getLines().stream()
                .map(l -> new OrderLine(l.getSku(), l.getQuantity(), Money.of(l.getUnitPrice())))
                .toList();
        return Order.restore(new OrderId(e.getId()), e.getCustomerEmail(), lines, e.getCreatedAt(),
                OrderStatus.valueOf(e.getStatus()), e.getCancellationReason());
    }
}
