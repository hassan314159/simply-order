package dev.simplyoder.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.simplyoder.order.controller.dto.CreateOrderRequest;
import dev.simplyoder.order.infra.outbox.OutboxEntity;
import dev.simplyoder.order.infra.outbox.OutboxRepository;
import dev.simplyoder.order.model.CreateOrderCommand;
import dev.simplyoder.order.model.OrderStatus;
import dev.simplyoder.order.persistence.OrderEntity;
import dev.simplyoder.order.persistence.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final OutboxRepository outboxRepo;
    private final ObjectMapper objectMapper;


    public OrderService(OrderRepository orderRepo, OutboxRepository outboxRepo, ObjectMapper objectMapper){
        this.orderRepo = orderRepo;
        this.outboxRepo = outboxRepo;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public UUID createOrder(CreateOrderRequest request) throws JsonProcessingException {

        UUID orderId = UUID.randomUUID();
        CreateOrderCommand cmd = CreateOrderCommand.from(orderId, request);
        OrderEntity order = OrderEntity.create(cmd);
        orderRepo.save(order);

        String payload = objectMapper.writeValueAsString(order);
        outboxRepo.save(OutboxEntity.pending("OrderCreated", orderId, payload));

        return orderId;

    }

    public void updateOrderStatus(UUID orderId, OrderStatus status){
        orderRepo.findById(orderId).ifPresent(o -> o.setStatus(status));
    }

    public OrderEntity findOrderById(UUID orderId){
        return orderRepo.findById(orderId).orElseThrow();
    }
}
