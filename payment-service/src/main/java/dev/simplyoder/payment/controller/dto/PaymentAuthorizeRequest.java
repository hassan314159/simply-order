package dev.simplyoder.payment.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentAuthorizeRequest(UUID orderId, BigDecimal amount) {
}