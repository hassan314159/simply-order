package dev.simplyoder.api.api.model;

public record InventoryItem(
        String sku,
        String name,
        String description,
        Integer availableQty
) {}
