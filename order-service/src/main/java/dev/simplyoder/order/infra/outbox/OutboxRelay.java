package dev.simplyoder.order.infra.outbox;

import dev.simplyoder.order.temporal.workflow.OrderWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowServiceException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

import static io.temporal.api.enums.v1.WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_REJECT_DUPLICATE;

@Component
public class OutboxRelay {

    // tune these
    private final int batchSize = 100;
    private final int maxAttempts = 15;

    private final OutboxRepository outboxRepo;
    private final WorkflowClient temporalClient;

    public OutboxRelay(OutboxRepository outboxRepo, WorkflowClient temporalClient) {
        this.outboxRepo = outboxRepo;
        this.temporalClient = temporalClient;
    }

    @Scheduled(fixedDelay = 500)
    public void orderWorkFlowSchedular() {
        List<OutboxEntity> items = outboxRepo.lockNextBatch(Instant.now(), batchSize);
        for (OutboxEntity ob : items) {
            try {
                String workflowId = "order-" + ob.getAggregateId();

                WorkflowOptions options = WorkflowOptions.newBuilder()
                        .setTaskQueue("order-task-queue")
                        .setWorkflowId(workflowId)
                        .setWorkflowIdReusePolicy(WORKFLOW_ID_REUSE_POLICY_REJECT_DUPLICATE)
                        .build();

                OrderWorkflow wf = temporalClient.newWorkflowStub(OrderWorkflow.class, options);

                wf.placeOrder(ob.getPayload());
                ob.markSent();

            } catch (WorkflowServiceException e) {
                // Workflow already exists with the same ID? Treat as success (idempotent start).
                if (isAlreadyStarted(e)) {
                    ob.markSent();
                } else {
                    // backoff
                    if (ob.getAttempts() + 1 >= maxAttempts) {
                        ob.fail();
                    } else {
                        ob.reschedule(nextBackoff(ob.getAttempts()));
                    }
                }
            } catch (Exception e) {
                if (ob.getAttempts() + 1 >= maxAttempts) {
                    ob.fail();
                } else {
                    ob.reschedule(nextBackoff(ob.getAttempts()));
                }
            }
        }
        outboxRepo.saveAll(items);
    }

    private boolean isAlreadyStarted(WorkflowServiceException e) {
        return e.getMessage() != null && e.getMessage().contains("Workflow execution already started");
    }

    private Instant nextBackoff(int attempts) {
        long delaySec = (long) Math.min(60 * 10, Math.pow(2, attempts)); // cap at 10m
        return Instant.now().plusSeconds(delaySec);
    }
}

