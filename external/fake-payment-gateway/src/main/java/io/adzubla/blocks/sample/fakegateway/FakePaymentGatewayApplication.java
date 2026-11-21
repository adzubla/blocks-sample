package io.adzubla.blocks.sample.fakegateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * A dummy payment provider. It stands in for a real external system, so it is deliberately
 * tiny and has no DDD structure: it isn't part of our domain.
 */
@SpringBootApplication
public class FakePaymentGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(FakePaymentGatewayApplication.class, args);
    }
}
