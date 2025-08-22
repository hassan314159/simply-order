package dev.simplyoder.order.clients;

import org.springframework.web.service.annotation.GetExchange;

public interface InventoryApi {

    @GetExchange("/reserve")
    void reserve();
}
