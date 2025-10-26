package dev.simplyoder.inventory.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // 409
public class InsufficientStockException extends IllegalStateException {
    public InsufficientStockException(String message) { super(message); }
}
