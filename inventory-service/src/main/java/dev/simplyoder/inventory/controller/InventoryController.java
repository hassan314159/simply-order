package dev.simplyoder.inventory.controller;

import dev.simplyoder.inventory.controller.dto.ReservationsRequest;
import dev.simplyoder.inventory.controller.dto.ReservationsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    Logger LOG = LoggerFactory.getLogger(InventoryController.class);

    @PostMapping("/reservations")
    public ResponseEntity<ReservationsResponse> create(@RequestBody ReservationsRequest req) {
        if(req.items().size() < 3){
            LOG.info("Items reserved successfully");
            return ResponseEntity.ok(new ReservationsResponse(UUID.randomUUID()));
        }else {
            LOG.info("Items could not be reserved");
            return ResponseEntity.status(209).build();
        }
    }

    @PostMapping("/reservations/{reservationsId}/release")
    public ResponseEntity<?> get(@PathVariable UUID reservationsId) {
        LOG.info("Items released for reservation: {}", reservationsId);
        return ResponseEntity.noContent().build();
    }
}