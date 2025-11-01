package dev.simplyoder.inventory.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IdempotencyRepository extends JpaRepository<IdempotencyEntity, UUID> {
}
