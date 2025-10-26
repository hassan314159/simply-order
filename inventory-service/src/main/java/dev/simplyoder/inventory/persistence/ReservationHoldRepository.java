package dev.simplyoder.inventory.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationHoldRepository extends JpaRepository<ReservationHoldEntity, Long> {
    List<ReservationHoldEntity> findByReservationId(UUID reservationId);
    void deleteByReservationId(UUID reservationId);
    Optional<ReservationHoldEntity> findByIdempotencyKey(String idempotencyKey);

}
