package dev.simplyoder.order.controller;

import dev.simplyoder.order.controller.dto.CreateOrderRequest;
import dev.simplyoder.order.controller.dto.CreateOrderResponse;
import dev.simplyoder.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {


    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> create(@RequestBody CreateOrderRequest req) {
        UUID id = orderService.createOrder(req);
        return ResponseEntity.status(201).body(new CreateOrderResponse(id, orderService.findOrderById(id).getStatus().name()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable UUID id) {
        return orderService.findOrderById(id) != null ? ResponseEntity.ok(orderService.findOrderById(id)) :
               ResponseEntity.notFound().build();
    }
}