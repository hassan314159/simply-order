package dev.simplyoder.api.infra.client.inventory.dto;

public record InventoryDto(
        String sku,
        String name,
        String description,
        Integer availableQty
) {}
