package dev.simplyoder.api.api;

import dev.simplyoder.api.api.model.InventoryItem;
import dev.simplyoder.api.api.model.Order;
import dev.simplyoder.api.infra.client.inventory.InventoryClient;
import dev.simplyoder.api.infra.client.order.OrderClient;
import dev.simplyoder.api.mapper.InventoryMapper;
import dev.simplyoder.api.mapper.OrderMapper;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
public class OrderQueryController {

    private final OrderClient orderClient;
    private final InventoryClient inventoryClient;
    private final OrderMapper orderMapper;
    private final InventoryMapper inventoryMapper;

    public OrderQueryController(OrderClient orderClient, InventoryClient inventoryClient, OrderMapper orderMapper, InventoryMapper inventoryMapper) {
        this.orderClient = orderClient;
        this.inventoryClient = inventoryClient;
        this.orderMapper = orderMapper;
        this.inventoryMapper = inventoryMapper;
    }

    @QueryMapping
    public Mono<Order> order(@Argument String id) {
        return orderClient.getOrder(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Order not found")))
                .map(orderMapper::toModel);
    }

    @QueryMapping
    public Mono<List<Order>> ordersByCustomer(@Argument String customerId,
                                              @Argument Integer limit) {
        int actualLimit = (limit == null) ? 20 : limit;
        return orderClient.getOrdersByCustomer(customerId, actualLimit)
                .map(list -> list.stream()
                        .map(orderMapper::toModel)
                        .toList());
    }

    @QueryMapping
    public Mono<InventoryItem> product(@Argument String sku) {
        return inventoryClient.getBySku(sku)
                .map(inventoryMapper::toModel);
    }
}
