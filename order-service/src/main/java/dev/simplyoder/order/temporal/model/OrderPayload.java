package dev.simplyoder.order.temporal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderPayload(UUID id, UUID customerId, BigDecimal total, List<Item> items) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            String sku,
            int quantity,
            BigDecimal price
    ) {}
}
