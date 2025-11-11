package dev.simplyoder.order.service.query;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponse(
        String id, String customerId, String createdAt,
        String status, BigDecimal totalAmount, List<OrderItemResponse> items
) {}
