package dev.simplyoder.order.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class Order {

    private UUID id;
    private UUID customerId;
    private Status status;
    private BigDecimal total;
    private List<Item> items;

    // no-args constructor
    public Order() { }

    // all-args constructor (same component order as the record)
    public Order(UUID id, UUID customerId, Status status, BigDecimal total, List<Item> items) {
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.total = total;
        this.items = items;
    }

    // getters
    public UUID getId() { return id; }
    public UUID getCustomerId() { return customerId; }
    public Status getStatus() { return status; }
    public BigDecimal getTotal() { return total; }
    public List<Item> getItems() { return items; }

    // setters
    public void setId(UUID id) { this.id = id; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public void setStatus(Status status) { this.status = status; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public void setItems(List<Item> items) { this.items = items; }

    // nested types kept as in the record
    public static class Item {
        private String sku;
        private int qty;
        private BigDecimal price;

        public Item() { }

        public Item(String sku, int qty, BigDecimal price) {
            this.sku = sku;
            this.qty = qty;
            this.price = price;
        }

        public String getSku() { return sku; }
        public int getQty() { return qty; }
        public BigDecimal getPrice() { return price; }

        public void setSku(String sku) { this.sku = sku; }
        public void setQty(int qty) { this.qty = qty; }
        public void setPrice(BigDecimal price) { this.price = price; }
    }

    public static enum Status {
        OPEN,
        PENDING,
        ITEMS_PROCESSED,
        PAYMENT_SUCCESSFUL,
        PAYMENT_FAILED,
        PROCESSING_FAILED,
        FAILED_UNKNOWN,
        COMPLETED
    }
}
