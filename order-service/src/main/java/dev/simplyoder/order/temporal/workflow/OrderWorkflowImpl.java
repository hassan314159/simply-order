package dev.simplyoder.order.temporal.workflow;


import dev.simplyoder.order.model.Order;
import dev.simplyoder.order.temporal.activities.OrderActivities;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;

import java.time.Duration;
import java.util.UUID;

public class OrderWorkflowImpl implements OrderWorkflow {
    private final OrderActivities act;

    public OrderWorkflowImpl() {
        var retry = RetryOptions.newBuilder().setMaximumAttempts(5).setBackoffCoefficient(2.0).setInitialInterval(Duration.ofSeconds(1)).build();
        var opts = ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofMinutes(2)).setRetryOptions(retry).build();
        this.act = Workflow.newActivityStub(OrderActivities.class, opts);
    }

    @Override
    public void placeOrder(Input in) {
        String sagaId = Workflow.getInfo().getWorkflowId();
        Saga saga = new Saga(new Saga.Options.Builder()
                .setContinueWithError(true) // collect/continue if a compensation fails
                .build());
        try {
            act.updateOrderStatus(in.orderId(), Order.Status.PENDING);
            UUID reservationId = act.reserveInventory(in.orderId(), sagaId, in.request().items());
            saga.addCompensation(() -> act.releaseInventoryIfAny(in.orderId(), reservationId));
            act.updateOrderStatus(in.orderId(), Order.Status.INVENTORY_RESERVED);

            UUID authId = act.authorizePayment(in.orderId(), sagaId, in.total());
            saga.addCompensation(() -> act.voidPaymentIfAny(in.orderId(), authId));
            act.updateOrderStatus(in.orderId(), Order.Status.PAYMENT_AUTHORIZED);

            act.updateOrderStatus(in.orderId(), Order.Status.COMPLETED);
        } catch (Exception e) {
            try {
                saga.compensate();     // runs: voidPayment → releaseInventory
            } finally {
                act.updateOrderStatus(in.orderId(), classifyError(e)); // precise status
            }
            throw e;
        }
    }


    private Order.Status classifyError(Exception e) {
        if (e instanceof io.temporal.failure.ActivityFailure af) {
            Throwable cause = af.getCause();
            if (cause instanceof io.temporal.failure.ApplicationFailure app) {
                return switch (app.getType()) {
                    case "InventoryNotSufficient" -> Order.Status.INVENTORY_FAILED;
                    case "PaymentRefused" -> Order.Status.PAYMENT_FAILED;
                    default -> Order.Status.FAILED_UNKNOWN;
                };
            }
        }
        return Order.Status.FAILED_UNKNOWN;
    }

}
