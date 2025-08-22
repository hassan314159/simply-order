package dev.simplyoder.order.controller.dto;

import java.util.UUID;

public record CreateOrderResponse(UUID orderId, String status) {
}
