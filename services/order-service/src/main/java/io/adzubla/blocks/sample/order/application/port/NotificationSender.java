package io.adzubla.blocks.sample.order.application.port;

import io.adzubla.blocks.sample.order.domain.model.Order;

/** Outbound port: asks the notification service to tell the customer about their order. */
public interface NotificationSender {

    void orderConfirmed(Order order);

    void orderCancelled(Order order);
}
