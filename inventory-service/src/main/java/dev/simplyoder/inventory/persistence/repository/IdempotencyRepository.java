package dev.simplyoder.inventory.persistence.repository;

import dev.simplyoder.inventory.persistence.entity.IdempotencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IdempotencyRepository extends JpaRepository<IdempotencyEntity, UUID> {
}
