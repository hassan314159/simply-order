package dev.simplyoder.api.infra.client.order.dto;

public record OrderItemDto(
        String sku,
        Integer quantity,
        Double price
) {}
