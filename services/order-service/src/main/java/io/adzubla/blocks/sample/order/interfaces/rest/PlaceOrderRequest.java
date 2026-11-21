package io.adzubla.blocks.sample.order.interfaces.rest;

import io.adzubla.blocks.sample.order.application.PlaceOrderCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record PlaceOrderRequest(
        @NotBlank @Email String customerEmail,
        @NotEmpty List<@Valid Item> items) {

    public record Item(@NotBlank String sku, @Positive int quantity) {
    }

    PlaceOrderCommand toCommand() {
        return new PlaceOrderCommand(customerEmail,
                items.stream().map(i -> new PlaceOrderCommand.Item(i.sku(), i.quantity())).toList());
    }
}
