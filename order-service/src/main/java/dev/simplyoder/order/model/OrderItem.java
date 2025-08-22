package dev.simplyoder.order.model;


import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;
import java.util.UUID;

public class OrderItem {

    private UUID id;
    private String sku;
    private int qty;
    private BigDecimal price;
    private Currency currency;

    public OrderItem() { }

    public OrderItem(UUID id, String sku, int qty, BigDecimal price, Currency currency) {
        this.id = id;
        this.sku = sku;
        this.qty = qty;
        this.price = price;
        this.currency = currency;
    }

    // getters
    public UUID getId() { return id; }
    public String getSku() { return sku; }
    public int getQty() { return qty; }
    public BigDecimal getPrice() { return price; }
    public Currency getCurrency() { return currency; }

    // setters
    public void setId(UUID id) { this.id = id; }
    public void setSku(String sku) { this.sku = sku; }
    public void setQty(int qty) { this.qty = qty; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setCurrency(Currency currency) { this.currency = currency; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItem)) return false;
        OrderItem that = (OrderItem) o;
        return Objects.equals(id, that.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }

    @Override public String toString() {
        return "OrderItem{id=" + id + ", sku='" + sku + '\'' +
                ", qty=" + qty + ", price=" + price + ", currency=" + currency + "}";
    }
}
