package dev.simplyoder.order.temporal.activities;


import dev.simplyoder.order.controller.dto.CreateOrderRequest;
import dev.simplyoder.order.model.Order;
import io.temporal.activity.ActivityInterface;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@ActivityInterface
public interface OrderActivities {
    void updateOrderStatus(UUID orderId, Order.Status status);

    UUID reserveInventory(UUID orderId, String sagaId, List<CreateOrderRequest.Item> items);

    UUID authorizePayment(UUID orderId, String sagaId, BigDecimal total);

    void voidPaymentIfAny(UUID orderId, UUID paymentId);

    void releaseInventoryIfAny(UUID orderId, UUID reservationId);



}
