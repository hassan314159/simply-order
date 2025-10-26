package dev.simplyoder.inventory.persistence;


import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "inventory_item")
public class InventoryItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 64, nullable = false)
    private String sku;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    private int inStockQty;

    @Column(nullable = false)
    private int reservedQty;

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    public InventoryItemEntity() {}

    public InventoryItemEntity(String sku, String name, String description, int inStockQty) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.inStockQty = inStockQty;
        this.reservedQty = 0;
    }

    @PreUpdate
    @PrePersist
    public void touch() {
        this.updatedAt = Instant.now();
    }

    // Getters and Setters

    public Long getId() {  return id;  }
    public void setId(Long id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getInStockQty() { return inStockQty; }
    public void setInStockQty(int inStockQty) { this.inStockQty = inStockQty; }

    public int getReservedQty() { return reservedQty; }
    public void setReservedQty(int reservedQty) { this.reservedQty = reservedQty; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
