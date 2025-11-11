package dev.simplyoder.inventory.controller;

import dev.simplyoder.inventory.service.command.InventoryService;
import dev.simplyoder.inventory.controller.dto.ReservationsRequest;
import dev.simplyoder.inventory.controller.dto.ReservationsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationsResponse> create(@RequestBody ReservationsRequest req,
                                                       @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey) {
        return ResponseEntity.ok(inventoryService.reserve(req, idempotencyKey));
    }

    @PostMapping("/reservations/{reservationsId}/release")
    public ResponseEntity<?> get(@PathVariable UUID reservationsId) {
        inventoryService.release(reservationsId);
        return ResponseEntity.noContent().build();
    }
}