package dev.simplyoder.payment.controller.dto;

import java.util.UUID;

public record PaymentAuthorizeResponse(UUID authId, boolean hold) {
}
