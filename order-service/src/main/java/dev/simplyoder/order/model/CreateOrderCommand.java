package dev.simplyoder.order.model;

import dev.simplyoder.order.controller.dto.CreateOrderRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class CreateOrderCommand {

    private UUID id;
    private UUID customerId;
    private List<Item> items;

    // no-args constructor
    public CreateOrderCommand() { }

    // all-args constructor (same component order as the record)
    public CreateOrderCommand(UUID id, UUID customerId, List<Item> items) {
        this.id = id;
        this.customerId = customerId;
        this.items = items;
    }

    // getters
    public UUID getId() { return id; }
    public UUID getCustomerId() { return customerId; }
    public List<Item> getItems() { return items; }

    // setters
    public void setId(UUID id) { this.id = id; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public void setItems(List<Item> items) { this.items = items; }

    public static class Item {
        private String sku;
        private int quantity;
        private BigDecimal price;

        public Item() { }

        public Item(String sku, int quantity, BigDecimal price) {
            this.sku = sku;
            this.quantity = quantity;
            this.price = price;
        }

        public String getSku() { return sku; }
        public int getQuantity() { return quantity; }
        public BigDecimal getPrice() { return price; }

        public void setSku(String sku) { this.sku = sku; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public void setPrice(BigDecimal price) { this.price = price; }
    }

    public static CreateOrderCommand from(UUID orderId, CreateOrderRequest request){
        List<Item> items = request.items()
                .stream()
                .map(i -> new Item(i.sku(), i.qty(), i.price())).toList();
        return new CreateOrderCommand(orderId, request.customerId(), items);
    }
}
