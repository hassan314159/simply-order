package dev.simplyoder.api.mapper;

import dev.simplyoder.api.api.model.Order;
import dev.simplyoder.api.api.model.OrderItem;
import dev.simplyoder.api.api.model.OrderStatus;
import dev.simplyoder.api.infra.client.order.dto.OrderDto;
import dev.simplyoder.api.infra.client.order.dto.OrderItemDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public Order toModel(OrderDto dto) {
        List<OrderItem> items = dto.items() == null
                ? List.of()
                : dto.items().stream()
                .map(this::toModel)
                .toList();

        return new Order(
                dto.id(),
                dto.customerId(),
                dto.createdAt(),
                OrderStatus.valueOf(dto.status()),
                items,
                dto.totalAmount()
        );
    }

    private OrderItem toModel(OrderItemDto dto) {
        return new OrderItem(
                dto.sku(),
                dto.quantity(),
                dto.price(),
                null   // inventory will be added by @BatchMapping
        );
    }
}
