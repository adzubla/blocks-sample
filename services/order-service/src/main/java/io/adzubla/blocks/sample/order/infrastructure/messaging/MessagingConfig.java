package io.adzubla.blocks.sample.order.infrastructure.messaging;

import io.adzubla.blocks.sample.contracts.Queues;
import io.adzubla.blocks.sample.contracts.Topics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Declares the broker resources this service needs. Each service declares the topic it owns.
 * Producer and consumer both declare a queue, so neither depends on the other starting first.
 */
@Configuration
class MessagingConfig {

    @Bean
    NewTopic orderEventsTopic() {
        return TopicBuilder.name(Topics.ORDER_EVENTS).partitions(3).replicas(1).build();
    }

    @Bean
    Queue notificationEmailQueue() {
        return new Queue(Queues.NOTIFICATION_EMAIL, true);
    }
}
