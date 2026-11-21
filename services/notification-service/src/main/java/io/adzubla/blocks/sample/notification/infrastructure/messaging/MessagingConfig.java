package io.adzubla.blocks.sample.notification.infrastructure.messaging;

import io.adzubla.blocks.sample.contracts.Queues;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Declares the queue this service consumes. */
@Configuration
class MessagingConfig {

    @Bean
    Queue notificationEmailQueue() {
        return new Queue(Queues.NOTIFICATION_EMAIL, true);
    }
}
