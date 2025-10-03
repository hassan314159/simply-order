package dev.simplyoder.order.temporal.workflow;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.simplyoder.order.model.OrderStatus;
import dev.simplyoder.order.temporal.activities.OrderActivities;
import dev.simplyoder.order.temporal.model.OrderPayload;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.UUID;

public class OrderWorkflowImpl implements OrderWorkflow {
    private final OrderActivities act;
    private final ObjectMapper objectMapper;

    public OrderWorkflowImpl(ObjectMapper objectMapper) {
        var retry = RetryOptions.newBuilder().setMaximumAttempts(5).setBackoffCoefficient(2.0).setInitialInterval(Duration.ofSeconds(1)).build();
        var opts = ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofMinutes(2)).setRetryOptions(retry).build();
        this.act = Workflow.newActivityStub(OrderActivities.class, opts);
        this.objectMapper = objectMapper;
    }

    @Override
    public void placeOrder(String orderPayload) {
        String sagaId = Workflow.getInfo().getWorkflowId();
        Saga saga = new Saga(new Saga.Options.Builder()
                .setContinueWithError(true) // collect/continue if a compensation fails
                .build());

        final OrderPayload order;
        try {
            order = objectMapper.readValue(orderPayload, OrderPayload.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        try {

            // 1) Update status of Order to PENDING
            act.updateOrderStatus(order.id(), OrderStatus.PENDING);

            // 2) reserve the inventory (calling inventory service)
            UUID reservationId = act.reserveInventory(order.id(), sagaId, order.items());
            saga.addCompensation(() -> act.releaseInventoryIfAny(order.id(), reservationId));

            // 3) Update status of Order to INVENTORY_RESERVED
            act.updateOrderStatus(order.id(), OrderStatus.INVENTORY_RESERVED);

            // 4) authorize the payment (calling payment service)
            UUID authId = act.authorizePayment(order.id(), sagaId, order.total());
            saga.addCompensation(() -> act.voidPaymentIfAny(order.id(), authId));

            // 5) Update status of Order to PAYMENT_AUTHORIZED
            act.updateOrderStatus(order.id(), OrderStatus.PAYMENT_AUTHORIZED);

            // 6) Complete our saga by updating Order Status to COMPLETED
            act.updateOrderStatus(order.id(), OrderStatus.COMPLETED);
        } catch (Exception e) {
            try {
                saga.compensate();     // runs: voidPayment → releaseInventory
            } finally {
                act.updateOrderStatus(order.id(), classifyError(e)); // precise status
            }
            throw e;
        }
    }


    private OrderStatus classifyError(Exception e) {
        if (e instanceof io.temporal.failure.ActivityFailure af) {
            Throwable cause = af.getCause();
            if (cause instanceof io.temporal.failure.ApplicationFailure app) {
                return switch (app.getType()) {
                    case "InventoryNotSufficient" -> OrderStatus.INVENTORY_FAILED;
                    case "PaymentRefused" -> OrderStatus.PAYMENT_FAILED;
                    default -> OrderStatus.FAILED_UNKNOWN;
                };
            }
        }
        return OrderStatus.FAILED_UNKNOWN;
    }

}
