package dev.simplyoder.inventory.persistence.repository;

import dev.simplyoder.inventory.persistence.entity.InventoryItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InventorySearchRepository extends JpaRepository<InventoryItemEntity, Long> {

    InventoryItemEntity findBySku(String sku);

    List<InventoryItemEntity> findBySkuIn(List<String> skus);

    @Query("""
        select i from InventoryItemEntity i
        where (:sku is null or i.sku = :sku)
          and (:name is null or lower(i.name) like lower(concat('%', :name, '%')))
          and (:minAvailable is null or (coalesce(i.inStockQty, 0) - coalesce(i.reservedQty, 0)) >= :minAvailable)
        order by i.sku
        """)
    List<InventoryItemEntity> search(
            @Param("sku") String sku,
            @Param("name") String name,
            @Param("minAvailable") Integer minAvailable
    );
}
