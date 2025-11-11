package dev.simplyoder.order.api.dto;

import java.util.UUID;

public record CreateOrderResponse(UUID orderId, String status) {
}
