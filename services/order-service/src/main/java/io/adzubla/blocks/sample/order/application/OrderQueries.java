package io.adzubla.blocks.sample.order.application;

import io.adzubla.blocks.sample.order.domain.model.Order;
import io.adzubla.blocks.sample.order.domain.model.OrderId;
import io.adzubla.blocks.sample.order.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Read side of the order context. */
@Service
@Transactional(readOnly = true)
public class OrderQueries {

    private final OrderRepository orderRepository;

    public OrderQueries(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order get(OrderId id) {
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    public List<Order> list() {
        return orderRepository.findAll();
    }
}
