package dev.simplyoder.order.api.controller;

import dev.simplyoder.order.service.query.OrderQueryService;
import dev.simplyoder.order.service.query.OrderResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

    private final OrderQueryService orderQueryService;

    public OrderApiController(OrderQueryService orderQueryService){
        this.orderQueryService = orderQueryService;
    }

    @GetMapping
    public List<OrderResponse> search(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) OffsetDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) OffsetDateTime toDate,
            @RequestParam(required = false) String sku   // item sku
    ) {
        return orderQueryService.search(customerId, status, fromDate, toDate, sku);
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable UUID id) {
        return orderQueryService.findByOrderId(id)
                .orElseGet(() -> null);
    }
}
