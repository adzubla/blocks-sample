package io.adzubla.blocks.sample.inventory.infrastructure.messaging;

import io.adzubla.blocks.sample.contracts.Topics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/** Declares the topic this service owns. */
@Configuration
class MessagingConfig {

    @Bean
    NewTopic inventoryEventsTopic() {
        return TopicBuilder.name(Topics.INVENTORY_EVENTS).partitions(3).replicas(1).build();
    }
}
