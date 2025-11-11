package dev.simplyoder.inventory.service.query;

public record InventoryResponse(
        String sku,
        String name,
        String description,
        Integer availableQty
) {
}
