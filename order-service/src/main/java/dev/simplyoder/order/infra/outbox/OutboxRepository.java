package dev.simplyoder.order.infra.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEntity, UUID> {

    @Query(
            value = """
      SELECT * FROM outbox
      WHERE status = 'PENDING' AND available_at <= :now
      ORDER BY created_at
      FOR UPDATE SKIP LOCKED
      LIMIT :batchSize
    """,
            nativeQuery = true
    )
    List<OutboxEntity> lockNextBatch(@Param("now") Instant now,
                                     @Param("batchSize") int batchSize);
}
