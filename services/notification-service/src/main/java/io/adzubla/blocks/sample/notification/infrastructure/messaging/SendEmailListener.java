package io.adzubla.blocks.sample.notification.infrastructure.messaging;

import io.adzubla.blocks.sample.contracts.Queues;
import io.adzubla.blocks.sample.contracts.notification.SendEmail;
import io.adzubla.blocks.sample.notification.application.SendEmailUseCase;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

/** Adapter: carries out the {@code SendEmail} commands from RabbitMQ. */
@Component
class SendEmailListener {

    private final SendEmailUseCase sendEmail;
    private final JsonMapper json;

    SendEmailListener(SendEmailUseCase sendEmail, JsonMapper json) {
        this.sendEmail = sendEmail;
        this.json = json;
    }

    // TODO(idempotency): @Idempotent(header = MessageHeaders.EVENT_ID). A redelivered command
    // sends the email twice.
    // Phase 1: if SMTP fails, the message is requeued and retried in a tight loop. No DLQ.
    @RabbitListener(queues = Queues.NOTIFICATION_EMAIL)
    void on(String payload) {
        var command = json.readValue(payload, SendEmail.class);
        sendEmail.send(command.to(), command.subject(), command.body());
    }
}
