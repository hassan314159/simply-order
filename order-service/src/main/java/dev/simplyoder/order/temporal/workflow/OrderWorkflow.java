package dev.simplyoder.order.temporal.workflow;


import dev.simplyoder.order.controller.dto.CreateOrderRequest;
import dev.simplyoder.order.model.Order;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.math.BigDecimal;
import java.util.UUID;

@WorkflowInterface
public interface OrderWorkflow {
    record Input(UUID orderId, UUID customerId, BigDecimal total, CreateOrderRequest request) {
        public static Input from(Order order, CreateOrderRequest req) {
            return new Input(order.getId(), order.getCustomerId(), order.getTotal(), req);
        }
    }

    @WorkflowMethod
    void placeOrder(Input input);
}
