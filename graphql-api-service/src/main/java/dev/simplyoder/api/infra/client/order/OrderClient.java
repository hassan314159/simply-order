package dev.simplyoder.api.infra.client.order;

import dev.simplyoder.api.infra.client.order.dto.OrderDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class OrderClient {

    private final WebClient orderWebClient;

    public OrderClient(WebClient orderWebClient){
        this.orderWebClient = orderWebClient;
    }

    public Mono<OrderDto> getOrder(String id) {
        return orderWebClient.get()
                .uri("/api/orders/{id}", id)
                .retrieve()
                .bodyToMono(OrderDto.class);
    }

    public Mono<java.util.List<OrderDto>> getOrdersByCustomer(String customerId, int limit) {
        return orderWebClient.get()
                .uri(uri -> uri.path("/api/orders")
                        .queryParam("customerId", customerId)
                        .queryParam("limit", limit)
                        .build())
                .retrieve()
                .bodyToFlux(OrderDto.class)
                .collectList();
    }
}
