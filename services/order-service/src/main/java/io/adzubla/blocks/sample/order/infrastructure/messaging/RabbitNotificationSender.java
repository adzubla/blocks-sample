package io.adzubla.blocks.sample.order.infrastructure.messaging;

import io.adzubla.blocks.sample.contracts.MessageHeaders;
import io.adzubla.blocks.sample.contracts.Queues;
import io.adzubla.blocks.sample.contracts.notification.SendEmail;
import io.adzubla.blocks.sample.order.application.port.NotificationSender;
import io.adzubla.blocks.sample.order.domain.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

/**
 * Adapter: sends {@code SendEmail} commands to RabbitMQ. A command has exactly one consumer,
 * so it goes to a queue (through the default exchange), not to a Kafka topic.
 */
@Component
class RabbitNotificationSender implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(RabbitNotificationSender.class);

    private final RabbitTemplate rabbit;
    private final JsonMapper json;

    RabbitNotificationSender(RabbitTemplate rabbit, JsonMapper json) {
        this.rabbit = rabbit;
        this.json = json;
    }

    @Override
    public void orderConfirmed(Order order) {
        send(order, "Your order " + order.id() + " is confirmed",
                "Thank you! Your order of " + order.total().amount() + " is confirmed.");
    }

    @Override
    public void orderCancelled(Order order) {
        send(order, "Your order " + order.id() + " was cancelled",
                "Sorry, your order was cancelled. Reason: " + order.cancellationReason());
    }

    private void send(Order order, String subject, String body) {
        var command = new SendEmail(order.customerEmail(), subject, body);
        rabbit.convertAndSend(Queues.NOTIFICATION_EMAIL, json.writeValueAsString(command), message -> {
            var props = message.getMessageProperties();
            props.setHeader(MessageHeaders.EVENT_ID, UUID.randomUUID().toString());
            props.setHeader(MessageHeaders.EVENT_TYPE, SendEmail.class.getSimpleName());
            props.setHeader(MessageHeaders.CORRELATION_ID, order.id().toString());
            props.setHeader(MessageHeaders.CAUSATION_ID, MessageContext.causationId());
            return message;
        });
        log.info("Sent SendEmail command for order {}", order.id());
    }
}
