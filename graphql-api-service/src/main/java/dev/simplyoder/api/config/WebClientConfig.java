package dev.simplyoder.api.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties
public class WebClientConfig {

    private final ClientsProperties props;

    public WebClientConfig(ClientsProperties props) {
        this.props = props;
    }

    @Bean
    public WebClient orderWebClient() {
        return WebClient.builder()
                .baseUrl(props.getOrder().getBaseUrl())
                .build();
    }

    @Bean
    public WebClient inventoryWebClient() {
        return WebClient.builder()
                .baseUrl(props.getInventory().getBaseUrl())
                .build();
    }
}
