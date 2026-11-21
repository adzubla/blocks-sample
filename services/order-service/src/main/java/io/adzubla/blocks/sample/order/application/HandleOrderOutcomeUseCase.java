package io.adzubla.blocks.sample.order.application;

import io.adzubla.blocks.sample.order.application.port.NotificationSender;
import io.adzubla.blocks.sample.order.domain.model.Order;
import io.adzubla.blocks.sample.order.domain.model.OrderId;
import io.adzubla.blocks.sample.order.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case: settle an order once the other bounded contexts have reported back.
 * <p>
 * Phase 1 uses choreography: these methods react to events that inventory and payment publish,
 * and the order workflow is spread across the three services' listeners.
 * TODO(saga): replace with an orchestrator (an {@code OrderSaga} in this package) that sends
 * commands and keeps the whole workflow, its state and its timeouts in one place.
 */
@Service
public class HandleOrderOutcomeUseCase {

    private final OrderRepository orderRepository;
    private final NotificationSender notificationSender;

    public HandleOrderOutcomeUseCase(OrderRepository orderRepository, NotificationSender notificationSender) {
        this.orderRepository = orderRepository;
        this.notificationSender = notificationSender;
    }

    @Transactional
    public void stockReservationFailed(OrderId id, String reason) {
        cancel(id, "Stock reservation failed: " + reason);
    }

    @Transactional
    public void paymentFailed(OrderId id, String reason) {
        cancel(id, "Payment failed: " + reason);
    }

    @Transactional
    public void paymentCompleted(OrderId id) {
        Order order = load(id);
        order.confirm();
        orderRepository.save(order);
        // TODO(outbox): the email command is sent even if this transaction later rolls back.
        notificationSender.orderConfirmed(order);
    }

    private void cancel(OrderId id, String reason) {
        Order order = load(id);
        order.cancel(reason);
        orderRepository.save(order);
        notificationSender.orderCancelled(order);
    }

    private Order load(OrderId id) {
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }
}
