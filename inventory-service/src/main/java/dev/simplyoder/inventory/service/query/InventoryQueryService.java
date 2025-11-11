package dev.simplyoder.inventory.service.query;

import dev.simplyoder.inventory.persistence.repository.InventorySearchRepository;
import dev.simplyoder.inventory.service.InventoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryQueryService {

    private final InventorySearchRepository inventorySearchRepository;
    private final InventoryMapper inventoryMapper;

    public InventoryQueryService(InventorySearchRepository inventorySearchRepository, InventoryMapper inventoryMapper) {
        this.inventorySearchRepository = inventorySearchRepository;
        this.inventoryMapper = inventoryMapper;
    }

    public InventoryResponse getBySku(String sku) {
        return
                inventoryMapper.toDto(inventorySearchRepository.findBySku(sku));
    }

    public List<InventoryResponse> getBySkus(List<String> skus) {
        return inventorySearchRepository.findBySkuIn(skus)
                .stream()
                .map(inventoryMapper::toDto)
                .toList();
    }

    public List<InventoryResponse> search(String sku,
                                            String name,
                                            Integer minAvailable) {
        return inventorySearchRepository.search(sku, name, minAvailable)
                .stream().map(inventoryMapper::toDto).toList();
    }
}
