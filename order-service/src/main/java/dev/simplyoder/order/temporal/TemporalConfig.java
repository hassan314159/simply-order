package dev.simplyoder.order.temporal;

import dev.simplyoder.order.temporal.activities.OrderActivities;
import dev.simplyoder.order.temporal.workflow.OrderWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TemporalConfig {

    @Bean
    WorkflowServiceStubs service(@Value("${app.temporal.address}") String address) {
        return WorkflowServiceStubs.newServiceStubs(
                io.temporal.serviceclient.WorkflowServiceStubsOptions.newBuilder()
                        .setTarget(address).build());
    }

    @Bean
    WorkflowClient workflowClient(WorkflowServiceStubs service) {
        return WorkflowClient.newInstance(service);
    }

    @Bean
    WorkerFactory workerFactory(WorkflowClient client) {
        return WorkerFactory.newInstance(client);
    }

    @Bean
    Worker worker(WorkerFactory factory, OrderActivities activities) {
        Worker w = factory.newWorker("order-task-queue");
        w.registerWorkflowImplementationTypes(OrderWorkflowImpl.class);
        w.registerActivitiesImplementations(activities);
        factory.start();
        return w;
    }
}
