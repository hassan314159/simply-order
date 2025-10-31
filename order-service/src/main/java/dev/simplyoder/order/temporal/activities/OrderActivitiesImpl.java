package dev.simplyoder.order.temporal.activities;

import dev.simplyoder.order.model.OrderStatus;
import dev.simplyoder.order.service.OrderService;
import dev.simplyoder.order.temporal.model.OrderPayload;
import io.temporal.failure.ApplicationFailure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Duration;
import java.util.*;

@Component
public class OrderActivitiesImpl implements OrderActivities {

    private final RestTemplate http;

    private final String inventoryBase = System.getenv().getOrDefault("INVENTORY_BASE", "http://inventory-service:8080");
    private final String paymentBase = System.getenv().getOrDefault("PAYMENT_BASE", "http://payment-service:8080");
    @Autowired
    private final OrderService orderService;

    public OrderActivitiesImpl(OrderService orderService, RestTemplateBuilder builder) {
        this.orderService = orderService;
        this.http = builder
                .connectTimeout(Duration.ofSeconds(3))
                .readTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Override
    public void updateOrderStatus(UUID orderId, OrderStatus status) {
        orderService.updateOrderStatus(orderId, status);
    }

    @Override
    public UUID reserveInventory(UUID orderId, String sagaId, List<OrderPayload.Item> items) {
        record Item(String sku, int qty) {
        }
        record Req(UUID orderId, List<Item> items) {
        }
        record Res(UUID reservationId) {
        }
        var req = new Req(orderId, items.stream().map(i -> new Item(i.sku(), i.quantity())).toList());
        var headers = new HttpHeaders();
        headers.add("X-Idempotency-Key", sagaId + ":reserve");
        var res = http.exchange(URI.create(inventoryBase + "/inventory/reservations"), HttpMethod.POST, new HttpEntity<>(req, headers), Res.class);

        if (res.getStatusCode().value() == 209) {
            throw ApplicationFailure.newNonRetryableFailure("" , "InventoryNotSufficient");
        }
        return Objects.requireNonNull(res.getBody()).reservationId();
    }

    @Override
    public UUID authorizePayment(UUID orderId, String sagaId, BigDecimal total) {
        record Req(UUID orderId, BigDecimal amount) {
        }
        record Res(UUID authId, boolean hold) {
        }
        var req = new Req(orderId, total);
        var headers = new HttpHeaders();
        headers.add("X-Idempotency-Key", sagaId + ":pay");
        var res = http.exchange(URI.create(paymentBase + "/payments/authorize"), HttpMethod.POST, new HttpEntity<>(req, headers), Res.class);
        if (res.getStatusCode().isError()) throw new RuntimeException("Payment failed: " + res.getStatusCode());
        UUID authId = Objects.requireNonNull(res.getBody()).authId();
        if (res.getStatusCode().value() == 209) {
            throw ApplicationFailure.newNonRetryableFailure("" , "PaymentRefused");
        }
        return authId;
    }


    @Override
    public void voidPaymentIfAny(UUID orderId, UUID paymentId) {
        http.postForEntity(paymentBase + "/payments/" + paymentId + "/void", null, Void.class);
    }

    @Override
    public void releaseInventoryIfAny(UUID orderId, UUID reservationId) {
        http.postForEntity(inventoryBase + "/inventory/reservations/" + reservationId + "/release", null, Void.class);
    }
}
