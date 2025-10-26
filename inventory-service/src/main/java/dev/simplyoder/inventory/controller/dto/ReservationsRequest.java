package dev.simplyoder.inventory.controller.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ReservationsRequest(UUID orderId, List<Item> items) {
    public record Item(String sku, int qty, BigDecimal price) {
    }
}