package dev.simplyoder.inventory.controller;


import dev.simplyoder.inventory.persistence.entity.InventoryItemEntity;
import dev.simplyoder.inventory.service.query.InventoryQueryService;
import dev.simplyoder.inventory.service.query.InventoryResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryApiController {

    private final InventoryQueryService inventoryQueryService;

    public InventoryApiController(InventoryQueryService inventoryQueryService) {
        this.inventoryQueryService = inventoryQueryService;
    }

    @GetMapping("/{sku}")
    public InventoryResponse getOne(@PathVariable String sku) {
        return inventoryQueryService.getBySku(sku);
    }

    @GetMapping
    public List<InventoryResponse> search(
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer minAvailable
    ) {
        return inventoryQueryService.search(sku, name, minAvailable);
    }

    @PostMapping("/batch")
    public List<InventoryResponse> getBatch(@RequestBody SkusRequest request) {
        return inventoryQueryService.getBySkus(request.skus());
    }

    public record SkusRequest(List<String> skus) {}
}
