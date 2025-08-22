package dev.simplyoder.order.clients;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;

@Configuration
public class InventoryClientConfig {

    @Bean
    RestClient inventoryRestClient() {
        // Basic timeouts
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(3).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(5).toMillis());

        return RestClient.builder()
                .baseUrl("https://api.service-a.com")
                .requestFactory(factory)
                .defaultHeader("Accept", "application/json")
                // .defaultHeaders(h -> h.setBearerAuth("token")) // if you need auth
                .build();
    }


    @Bean
    InventoryApi inventoryApi(RestClient inventoryRestClient) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(inventoryRestClient))
                .build();
        return factory.createClient(InventoryApi.class);
    }
}
