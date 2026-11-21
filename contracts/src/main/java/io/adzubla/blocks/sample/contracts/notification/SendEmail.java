package io.adzubla.blocks.sample.contracts.notification;

/** Command: send an email. Sent to the {@code notification.email} RabbitMQ queue. */
public record SendEmail(String to, String subject, String body) {
}
