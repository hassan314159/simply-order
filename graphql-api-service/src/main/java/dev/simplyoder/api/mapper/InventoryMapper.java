package dev.simplyoder.api.mapper;

import dev.simplyoder.api.api.model.InventoryItem;
import dev.simplyoder.api.infra.client.inventory.dto.InventoryDto;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public InventoryItem toModel(InventoryDto dto) {
        return new InventoryItem(
                dto.sku(),
                dto.name(),
                dto.description(),
                dto.availableQty()
        );
    }
}
