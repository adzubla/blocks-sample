package io.adzubla.blocks.sample.notification.application;

import io.adzubla.blocks.sample.notification.application.port.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Use case: send an email. notification-service has no domain model of its own: it is a
 * technical service that carries out commands from the other services.
 */
@Service
public class SendEmailUseCase {

    private static final Logger log = LoggerFactory.getLogger(SendEmailUseCase.class);

    private final EmailSender emailSender;

    public SendEmailUseCase(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void send(String to, String subject, String body) {
        emailSender.send(to, subject, body);
        log.info("Sent email '{}' to {}", subject, to);
    }
}
