package dev.simplyoder.order.api.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(UUID customerId, List<Item> items) {
    public record Item(String sku, int qty, BigDecimal price) {
    }
}