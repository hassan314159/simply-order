package dev.simplyoder.order.service.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.simplyoder.order.api.dto.CreateOrderRequest;
import dev.simplyoder.order.infra.outbox.OutboxEntity;
import dev.simplyoder.order.infra.outbox.OutboxRepository;
import dev.simplyoder.order.persistence.entity.OrderEntity;
import dev.simplyoder.order.persistence.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class OrderCommandService {

    private final OrderRepository orderRepo;
    private final OutboxRepository outboxRepo;
    private final ObjectMapper objectMapper;


    public OrderCommandService(OrderRepository orderRepo, OutboxRepository outboxRepo, ObjectMapper objectMapper){
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
        orderRepo.findById(orderId).ifPresent(o -> {
            o.setStatus(status);
            orderRepo.save(o);
        });
    }

    public OrderEntity findOrderById(UUID orderId){
        return orderRepo.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Order %s not found".formatted(orderId)));
    }
}
