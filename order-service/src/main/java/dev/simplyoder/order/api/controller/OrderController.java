package dev.simplyoder.order.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.simplyoder.order.api.dto.CreateOrderRequest;
import dev.simplyoder.order.api.dto.CreateOrderResponse;
import dev.simplyoder.order.service.command.OrderCommandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {


    private final OrderCommandService orderCommandService;

    public OrderController(OrderCommandService orderCommandService){
        this.orderCommandService = orderCommandService;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> create(@RequestBody CreateOrderRequest req) throws JsonProcessingException {
        UUID id = orderCommandService.createOrder(req);
        return ResponseEntity.status(201).body(new CreateOrderResponse(id, orderCommandService.findOrderById(id).getStatus().name()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable UUID id) {
        return orderCommandService.findOrderById(id) != null ? ResponseEntity.ok(orderCommandService.findOrderById(id)) :
               ResponseEntity.notFound().build();
    }
}