package dev.simplyoder.order.service.query;

import java.math.BigDecimal;

public record OrderItemResponse(String sku, int quantity, BigDecimal price) {}