package dev.simplyoder.order.service;

import dev.simplyoder.order.controller.dto.CreateOrderRequest;
import dev.simplyoder.order.model.Order;
import dev.simplyoder.order.temporal.workflow.OrderWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService {

    public Map<UUID, Order> orders = new ConcurrentHashMap<>();
    private final WorkflowClient client;


    public OrderService(WorkflowClient client){
        this.client = client;
    }

    public UUID createOrder(CreateOrderRequest request){
        UUID orderId = UUID.randomUUID();
        BigDecimal total = BigDecimal.ZERO;
        List<Order.Item> items = new LinkedList<>();
        for (var it : request.items()) {
            total = total.add(it.price().multiply(BigDecimal.valueOf(it.qty())));
            items.add(new Order.Item(it.sku(), it.qty(), it.price()));
        }
        Order order = new Order(orderId, request.customerId(), Order.Status.OPEN, total, items);
        // simple map acts as in in-memory store
        orders.put(orderId, order);

        // start oder creation saga ** WHEN INTRODUCE DB WILL BE UPDATED TO BE STARTED BY OUTBOX RELAY
        OrderWorkflow wf = client.newWorkflowStub(
                OrderWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue("order-task-queue")
                        .setWorkflowId("order-" + orderId) // This is saga id
                        .build());

        WorkflowClient.start(wf::placeOrder, OrderWorkflow.Input.from(order, request));
        return orderId;

    }

    public Order findOrderById(UUID orderId){
        return orders.get(orderId);
    }
}
