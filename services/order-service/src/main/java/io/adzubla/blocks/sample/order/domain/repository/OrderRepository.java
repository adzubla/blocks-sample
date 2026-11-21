package io.adzubla.blocks.sample.order.domain.repository;

import io.adzubla.blocks.sample.order.domain.model.Order;
import io.adzubla.blocks.sample.order.domain.model.OrderId;

import java.util.List;
import java.util.Optional;

/** Collection-like access to {@link Order} aggregates. Implemented in infrastructure. */
public interface OrderRepository {

    void save(Order order);

    Optional<Order> findById(OrderId id);

    List<Order> findAll();
}
