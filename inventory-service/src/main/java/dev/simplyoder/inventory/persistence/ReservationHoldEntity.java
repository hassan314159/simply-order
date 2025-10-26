package dev.simplyoder.inventory.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "reservation_hold")
public class ReservationHoldEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID reservationId;

    @Column(name="order_id", nullable=false)
    private String orderId;

    @Column(name="idempotency_key")
    private String idempotencyKey;

    @Column(nullable = false, length = 64)
    private String sku;

    @Column(nullable = false)
    private int qty;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private State state; // PENDING | CONFIRMED | CANCELLED


    public enum State { PENDING, CONFIRMED, CANCELLED }

    public static ReservationHoldEntity reserve(UUID reservationId, String orderId, String idempotencyKey, String sku, int qty){
        ReservationHoldEntity entity = new ReservationHoldEntity();
        entity.setState(State.PENDING);
        entity.setReservationId(reservationId);
        entity.setIdempotencyKey(idempotencyKey);
        entity.setOrderId(orderId);
        entity.setSku(sku);
        entity.setQty(qty);
        return  entity;
    }

    public void confirm(){
        this.state = State.CONFIRMED;
    }

    public void release(){
        this.state = State.CANCELLED;
    }

    public boolean isReleasable(){
        return this.state == State.PENDING;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UUID getReservationId() { return reservationId; }
    public void setReservationId(UUID reservationId) { this.reservationId = reservationId; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }

    public State getState() { return state; }
    public void setState(State state) { this.state = state; }
}
