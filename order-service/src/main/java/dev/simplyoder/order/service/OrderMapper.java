package dev.simplyoder.order.service;

import dev.simplyoder.order.persistence.entity.OrderEntity;
import dev.simplyoder.order.service.command.OrderStatus;
import dev.simplyoder.order.service.query.OrderApiStatus;
import dev.simplyoder.order.service.query.OrderItemResponse;
import dev.simplyoder.order.service.query.OrderResponse;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderApiStatus toApiStatus(OrderStatus internal) {
        return switch (internal) {
            case OPEN, PENDING -> OrderApiStatus.NEW;
            case INVENTORY_RESERVED, PAYMENT_AUTHORIZED -> OrderApiStatus.PROCESSING;
            case COMPLETED -> OrderApiStatus.COMPLETED;
            default -> OrderApiStatus.FAILED;
        };
    }

    public OrderResponse toDto(OrderEntity e) {
        var items = e.getItems().stream()
                .map(i -> new OrderItemResponse(i.getSku(), i.getQuantity(), i.getPrice()))
                .toList();
        return new OrderResponse(
                e.getId().toString(), e.getCustomerId().toString(), e.getCreatedAt().toString(),
                toApiStatus(e.getStatus()).name(), e.getTotal(), items);
    }
}
