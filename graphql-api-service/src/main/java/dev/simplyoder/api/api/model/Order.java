package dev.simplyoder.api.api.model;

import java.util.List;

public record Order(
        String id,
        String customerId,
        String createdAt,
        OrderStatus status,
        List<OrderItem> items,
        Double totalAmount
) {}
