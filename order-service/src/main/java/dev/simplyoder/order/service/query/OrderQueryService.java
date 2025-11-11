package dev.simplyoder.order.service.query;

import dev.simplyoder.order.service.OrderMapper;
import dev.simplyoder.order.persistence.entity.OrderEntity;
import dev.simplyoder.order.persistence.repository.OrderSearchRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderQueryService {

    private final OrderSearchRepository orderSearchRepository;
    public final OrderMapper orderMapper;

    public OrderQueryService(OrderSearchRepository orderSearchRepository, OrderMapper orderMapper){
        this.orderSearchRepository = orderSearchRepository;
        this.orderMapper = orderMapper;
    }

    public Optional<OrderResponse> findByOrderId(UUID orderId) {
        return orderSearchRepository.findById(orderId).map(orderMapper::toDto);
    }

    public List<OrderResponse> search(String customerId, String status, OffsetDateTime fromDate, OffsetDateTime toDate, String sku) {
        return orderSearchRepository.search(customerId, status, fromDate, toDate, sku)
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

}
