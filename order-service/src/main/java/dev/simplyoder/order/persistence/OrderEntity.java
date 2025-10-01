package dev.simplyoder.order.persistence;

import dev.simplyoder.order.model.OrderStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderEntity{

    @Id
    @GeneratedValue
    private UUID id;

    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private OrderStatus status = OrderStatus.OPEN;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> items = new ArrayList<OrderItemEntity>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public void addItem(OrderItemEntity item) {
        item.setOrder(this);
        items.add(item);
        recalcTotal();
    }

    public void removeItem(OrderItemEntity item) {
        items.remove(item);
        item.setOrder(null);
        recalcTotal();
    }

    public void recalcTotal() {
        this.total = items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
        recalcTotal();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
        recalcTotal();
    }
}
