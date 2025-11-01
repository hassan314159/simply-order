package dev.simplyoder.inventory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.simplyoder.inventory.controller.dto.ReservationsRequest;
import dev.simplyoder.inventory.controller.dto.ReservationsResponse;
import dev.simplyoder.inventory.controller.exception.InsufficientStockException;
import dev.simplyoder.inventory.controller.utils.ReservationIds;
import dev.simplyoder.inventory.persistence.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Service
public class InventoryService {

    private static final String OP_RESERVE = "RESERVE";

    private final InventoryItemRepository inventoryItemRepository;
    private final ReservationHoldRepository reservationHoldRepository;
    private final IdempotencyRepository idempotencyRepository;
    private final ObjectMapper objectMapper;

    public InventoryService(InventoryItemRepository inventoryItemRepository, ReservationHoldRepository reservationHoldRepository,
                            IdempotencyRepository idempotencyRepository, ObjectMapper objectMapper) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.reservationHoldRepository = reservationHoldRepository;
        this.idempotencyRepository = idempotencyRepository;
        this.objectMapper = objectMapper;
    }

    public ReservationsResponse reserve(ReservationsRequest req, String idempotencyKey) {

        final UUID rid = ReservationIds.fromKey(idempotencyKey, OP_RESERVE);

        Optional<IdempotencyEntity> idempotencyRow = idempotencyRepository.findById(rid);

        // handle idempotency
        if(idempotencyRow.isPresent()){
            return tryDeserialize(idempotencyRow.get())
                    .orElseGet(() -> new ReservationsResponse(rid));
        }

        // Safe check: if domain already created reservation (previous crash)
        if (!reservationHoldRepository.findByReservationId(rid).isEmpty()) {
            ReservationsResponse resp = new ReservationsResponse(rid);
            saveIdempotency(rid, req.orderId().toString(), HttpStatus.OK.value(), serializeResponse(resp));
            return resp;
        }

        try {
            ReservationsResponse resp = reserveDomainTransaction(req, rid, idempotencyKey); // @Transactional
            saveIdempotency(rid, req.orderId().toString(), HttpStatus.OK.value(), serializeResponse(resp)); // REQUIRES_NEW
            return resp;
        } catch (InsufficientStockException ex) {
            saveIdempotency(rid, req.orderId().toString(), HttpStatus.CONFLICT.value(), serializeResponse(Collections.EMPTY_MAP));     // REQUIRES_NEW
            throw ex;
        }
    }


    @Transactional
    public ReservationsResponse reserveDomainTransaction(ReservationsRequest req, UUID rid, String idempotencyKey){
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void saveIdempotency(UUID rid, String aggregateId, int httpStatus, String jsonResponse){
        IdempotencyEntity idempotencyEntity = IdempotencyEntity.create(rid, aggregateId, httpStatus, jsonResponse);
        idempotencyRepository.save(idempotencyEntity);
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

    private String serializeResponse(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            // As a last resort, store a minimal payload
            return "{\"error\":\"serialization_failed\"}";
        }
    }

    private Optional<ReservationsResponse> tryDeserialize(IdempotencyEntity idempotencyEntity) {
        String json = idempotencyEntity.getJsonResponse();
        if (json == null || json.isBlank()) return Optional.empty();
        try {
            return Optional.of(objectMapper.readValue(json, ReservationsResponse.class));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
