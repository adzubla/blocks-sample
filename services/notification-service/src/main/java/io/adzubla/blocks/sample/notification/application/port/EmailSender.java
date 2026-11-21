package io.adzubla.blocks.sample.notification.application.port;

/** Outbound port: the email provider (Mailpit over SMTP in this sample). */
public interface EmailSender {

    void send(String to, String subject, String body);
}
