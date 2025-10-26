package dev.simplyoder.inventory.service;

import dev.simplyoder.inventory.controller.dto.ReservationsRequest;
import dev.simplyoder.inventory.controller.dto.ReservationsResponse;
import dev.simplyoder.inventory.controller.exception.InsufficientStockException;
import dev.simplyoder.inventory.persistence.InventoryItemRepository;
import dev.simplyoder.inventory.persistence.ReservationHoldEntity;
import dev.simplyoder.inventory.persistence.ReservationHoldRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class InventoryService {

    private final InventoryItemRepository inventoryItemRepository;
    private final ReservationHoldRepository reservationHoldRepository;

    public InventoryService(InventoryItemRepository inventoryItemRepository, ReservationHoldRepository reservationHoldRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.reservationHoldRepository = reservationHoldRepository;
    }

    @Transactional
    public ReservationsResponse reserve(ReservationsRequest req, String idempotencyKey) {

        // check idempotency
        Optional<ReservationHoldEntity> reservation = reservationHoldRepository.findByIdempotencyKey(idempotencyKey);
        if(reservation.isPresent()){
            return new ReservationsResponse(reservation.get().getReservationId());
        }

        UUID rid =  UUID.randomUUID();
        req.items().forEach(item -> {
            var itemEntity = inventoryItemRepository.findBySku(item.sku()).orElseThrow();
            int available = itemEntity.getInStockQty() - itemEntity.getReservedQty();
            if (item.qty() > available) throw new InsufficientStockException("Insufficient stock for " + item.sku());
            itemEntity.setReservedQty(itemEntity.getReservedQty() + item.qty());
            inventoryItemRepository.save(itemEntity);
        });

        // persist the holds for later release-by-id
        var rows = new ArrayList<ReservationHoldEntity>();
        for (var it : req.items()) {
            var rh = ReservationHoldEntity.reserve(rid, req.orderId().toString(), idempotencyKey, it.sku(), it.qty());
            rows.add(rh);
        }
        reservationHoldRepository.saveAll(rows);

        return new ReservationsResponse(rid);
    }

    @Transactional
    public ReservationsResponse release(UUID reservationId) {
        var reservationHolds = reservationHoldRepository.findByReservationId(reservationId);

        reservationHolds
                .stream()
                .filter(ReservationHoldEntity::isReleasable) // Apply idempotency condition
                .forEach(reservationHold -> {
                    var itemEntity = inventoryItemRepository.findBySku(reservationHold.getSku()).orElseThrow();
                    if (reservationHold.getQty() > itemEntity.getReservedQty()) throw new InsufficientStockException("Cannot release more than reserved for " + reservationHold.getSku());
                    itemEntity.setReservedQty(itemEntity.getReservedQty() - reservationHold.getQty());
                    reservationHold.release();
                });

        return new ReservationsResponse(reservationId);
    }
}
