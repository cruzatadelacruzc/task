package aleph.engineering.note.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


import aleph.engineering.note.IntegrationTest;
import aleph.engineering.note.domain.Task;
import aleph.engineering.note.services.dto.TaskInput;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/*
 * Integration tests for {@link TaskService}.
 */
@IntegrationTest
public class TaskServiceIT {
    private final String DEFAULT_BODY = "THIS IS A DEFAULT BODY";
    private final String UPDATED_BODY = "THIS IS A UPDATED BODY";

    @Autowired
    private TaskService taskService;

    @Test
    void testGetTaskById() {
        Task savedTask = taskService.create(new TaskInput(null, DEFAULT_BODY)).block();

        Mono<Task> retrievedTaskMono = taskService.getTask(savedTask.id());

        StepVerifier.create(retrievedTaskMono)
            .expectNextMatches(task -> task.id().equals(savedTask.id()))
            .expectComplete()
            .verify();
    }

    @Test
    void testGetAllTasks() {
        final String BODY2 = "BODY 2";
        final String BODY3 = "BODY 3";
        taskService.create(new TaskInput(null, DEFAULT_BODY)).block();
        taskService.create(new TaskInput(null, BODY2)).block();
        taskService.create(new TaskInput(null, BODY3)).block();

        Flux<Task> tasksFlux = taskService.getAll();

        StepVerifier.create(tasksFlux)
            .expectNextCount(3)
            .expectComplete()
            .verify();

        StepVerifier.create(tasksFlux)
            .expectNextMatches(task -> task.body().equals(DEFAULT_BODY) ||
                                task.body().equals(BODY2) ||
                                task.body().equals(BODY3))
            .expectNextMatches(task -> task.body().equals(DEFAULT_BODY) ||
                                task.body().equals(BODY2) ||
                                task.body().equals(BODY3))
            .expectNextMatches(task -> task.body().equals(DEFAULT_BODY) ||
                                task.body().equals(BODY2) ||
                                task.body().equals(BODY3))
            .expectComplete()
            .verify();
    }

    @Test
    void testCreateTask() {
        // Use the service to create a new task
        TaskInput input = new TaskInput(null, DEFAULT_BODY);
        Mono<Task> createdTaskMono = taskService.create(input);

        // Use StepVerifier to assert the result
        StepVerifier.create(createdTaskMono)
            .expectNextMatches(task -> task.body().equals(input.body()))
            .expectComplete()
            .verify();
    }

    @Test
    void testUpdateTask() {
        // Create a task and save it to the repository
        Task savedTask = taskService.create(new TaskInput(null, DEFAULT_BODY)).block();

        // Update the task's body        
        Mono<Task> updatedTaskMono = taskService.update(new TaskInput(savedTask.id(), UPDATED_BODY));

        // Use StepVerifier to assert the result
        StepVerifier.create(updatedTaskMono)
            .expectNextMatches(task -> task.body().equals(UPDATED_BODY))
            .expectComplete()
            .verify();
    }
}
