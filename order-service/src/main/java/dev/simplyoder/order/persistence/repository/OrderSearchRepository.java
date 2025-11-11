package dev.simplyoder.order.persistence.repository;

import dev.simplyoder.order.persistence.entity.OrderEntity;
import dev.simplyoder.order.service.command.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderSearchRepository extends JpaRepository<OrderEntity, UUID> {

    @Query("""
        select distinct o from OrderEntity o
        left join o.items i
        where (:customerId is null or o.customerId = :customerId)
          and (:status     is null or o.status = :status)
          and (:fromDate   is null or o.createdAt >= :fromDate)
          and (:toDate     is null or o.createdAt < :toDate)
          and (:sku        is null or i.sku = :sku)
        order by o.createdAt desc
        """)
    List<OrderEntity> search(
            @Param("customerId") String customerId,
            @Param("status") String status,
            @Param("fromDate") OffsetDateTime fromDate,
            @Param("toDate") OffsetDateTime toDate,
            @Param("sku") String sku
    );
}
