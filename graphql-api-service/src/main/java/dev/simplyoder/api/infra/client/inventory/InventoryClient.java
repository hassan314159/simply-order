package dev.simplyoder.api.infra.client.inventory;

import dev.simplyoder.api.infra.client.inventory.dto.InventoryDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class InventoryClient {

    private final WebClient inventoryWebClient;

    public InventoryClient(WebClient inventoryWebClient) {
        this.inventoryWebClient = inventoryWebClient;
    }

    public Mono<InventoryDto> getBySku(String sku) {
        return inventoryWebClient.get()
                .uri("/api/inventory/{sku}", sku)
                .retrieve()
                .bodyToMono(InventoryDto.class);
    }

    public Mono<List<InventoryDto>> getBySkus(Collection<String> skus) {
        return inventoryWebClient.post()
                .uri("/api/inventory/batch")
                .bodyValue(Map.of("skus", skus))
                .retrieve()
                .bodyToFlux(InventoryDto.class)
                .collectList();
    }
}
