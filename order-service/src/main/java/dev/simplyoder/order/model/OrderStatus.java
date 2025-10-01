package dev.simplyoder.order.model;

public enum OrderStatus {
    OPEN,
    PENDING,
    INVENTORY_RESERVED,
    PAYMENT_AUTHORIZED,
    PAYMENT_FAILED,
    INVENTORY_FAILED,
    FAILED_UNKNOWN,
    COMPLETED
}
