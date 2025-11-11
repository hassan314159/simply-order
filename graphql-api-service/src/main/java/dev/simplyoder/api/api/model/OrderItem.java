package dev.simplyoder.api.api.model;

public record OrderItem(
        String sku,
        Integer quantity,
        Double price,
        InventoryItem inventory    // will be filled by @BatchMapping
) {}
