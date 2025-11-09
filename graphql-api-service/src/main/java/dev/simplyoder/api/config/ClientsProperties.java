package dev.simplyoder.api.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "clients")
@Configuration
public class ClientsProperties {

    private final ServiceProps order = new ServiceProps();
    private final ServiceProps inventory = new ServiceProps();

    public ServiceProps getOrder() { return order; }
    public ServiceProps getInventory() { return inventory; }

    public static class ServiceProps {
        private String baseUrl;
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    }
}