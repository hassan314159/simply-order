package dev.simplyoder.inventory.service;

import dev.simplyoder.inventory.persistence.entity.InventoryItemEntity;
import dev.simplyoder.inventory.service.query.InventoryResponse;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public InventoryResponse toDto(InventoryItemEntity e) {
        return new InventoryResponse(
                e.getSku(), e.getName(), e.getDescription(), e.getInStockQty() - e.getReservedQty());
    }
}
