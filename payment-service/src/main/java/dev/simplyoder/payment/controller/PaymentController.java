package dev.simplyoder.payment.controller;

import dev.simplyoder.payment.controller.dto.PaymentAuthorizeRequest;
import dev.simplyoder.payment.controller.dto.PaymentAuthorizeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    Logger LOG = LoggerFactory.getLogger(PaymentController.class);

    @PostMapping("/authorize")
    public ResponseEntity<PaymentAuthorizeResponse> create(@RequestBody PaymentAuthorizeRequest req) {
        if(req.amount().compareTo(BigDecimal.valueOf(500)) < 0){
            LOG.info("Payment processed successfully");
            return ResponseEntity.ok(new PaymentAuthorizeResponse(UUID.randomUUID(), false));
        }else {
            LOG.info("Payment Failed");
            return ResponseEntity.status(209).build();
        }
    }

    @PostMapping("/{paymentId}/void")
    public ResponseEntity<?> get(@PathVariable UUID authId) {
        LOG.info("Payment refunded: {}", authId);
        return ResponseEntity.noContent().build();
    }
}