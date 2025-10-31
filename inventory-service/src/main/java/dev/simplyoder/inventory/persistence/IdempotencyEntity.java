package dev.simplyoder.inventory.persistence;

import dev.simplyoder.inventory.controller.utils.ReservationIds;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "api_idempotency")
public class IdempotencyEntity {

    @Id
    private UUID key;
    private String aggregateId;
    private Integer httpStatus;
    @Column(length = 4000)
    private String jsonResponse;

    private Instant createdAt;
    private Instant updatedAt;




    public static IdempotencyEntity create(UUID key, String aggregateId, Integer httpStatus, String payload){
        IdempotencyEntity entity = new IdempotencyEntity();
        entity.setKey(key);
        entity.setAggregateId(aggregateId);
        entity.setHttpStatus(httpStatus);
        entity.setJsonResponse(payload);
        return entity;
    }


    @PrePersist
    public void onCreate() {
        Instant now = Instant.now();
        if (this.createdAt == null) this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getKey() {
        return key;
    }

    public void setKey(UUID key) {
        this.key = key;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getJsonResponse() {
        return jsonResponse;
    }

    public void setJsonResponse(String jsonResponse) {
        this.jsonResponse = jsonResponse;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
