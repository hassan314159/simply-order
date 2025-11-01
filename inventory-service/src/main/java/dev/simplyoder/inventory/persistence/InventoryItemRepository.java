package dev.simplyoder.inventory.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryItemRepository extends JpaRepository<InventoryItemEntity, Long> {
    Optional<InventoryItemEntity> findBySku(String sku);
    List<InventoryItemEntity> findBySkuIn(List<String> skus);
}
