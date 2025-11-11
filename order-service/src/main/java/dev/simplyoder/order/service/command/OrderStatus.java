package dev.simplyoder.order.service.command;

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
