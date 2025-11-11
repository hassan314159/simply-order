package dev.simplyoder.api.infra.client.order.dto;

import java.util.List;

public record OrderDto(
        String id,
        String customerId,
        String createdAt,
        String status,
        Double totalAmount,
        List<OrderItemDto> items
) {}