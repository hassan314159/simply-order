package dev.simplyoder.api.api;

import dev.simplyoder.api.api.model.InventoryItem;
import dev.simplyoder.api.api.model.OrderItem;
import dev.simplyoder.api.infra.client.inventory.InventoryClient;
import dev.simplyoder.api.mapper.InventoryMapper;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class OrderItemFieldResolver {

    private final InventoryClient inventoryClient;
    private final InventoryMapper inventoryMapper;

    public OrderItemFieldResolver(InventoryClient inventoryClient, InventoryMapper inventoryMapper) {
        this.inventoryClient = inventoryClient;
        this.inventoryMapper = inventoryMapper;
    }

    @BatchMapping(typeName = "OrderItem", field = "inventory")
    public Mono<Map<OrderItem, InventoryItem>> inventory(List<OrderItem> items) {
        // collect unique SKUs
        List<String> skus = items.stream()
                .map(OrderItem::sku)
                .distinct()
                .toList();

        return inventoryClient.getBySkus(skus)
                .map(inventoryDtos -> {
                    Map<String, InventoryItem> bySku = inventoryDtos.stream()
                            .map(inventoryMapper::toModel)
                            .collect(Collectors.toMap(InventoryItem::sku, inv -> inv));

                    Map<OrderItem, InventoryItem> result = new HashMap<>();
                    for (OrderItem item : items) {
                        result.put(item, bySku.get(item.sku()));
                    }
                    return result;
                });
    }
}
