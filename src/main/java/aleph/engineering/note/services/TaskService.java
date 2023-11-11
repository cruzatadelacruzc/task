package aleph.engineering.note.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import aleph.engineering.note.domain.Task;
import aleph.engineering.note.repositories.TaskRepository;
import aleph.engineering.note.services.dto.TaskInput;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service class for managing tasks.
 */
@Service
public class TaskService {
    private final TaskRepository repository;
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieve a task by its ID.
     *
     * @param id The ID of the task to retrieve.
     * @return A Mono emitting the task, or empty if not found.
     */
    public Mono<Task> getTask(String id) {
        log.debug("Service to get a Task, id {}", id);
        return repository.findById(id);
    }

    /**
     * Retrieve all tasks.
     *
     * @return A Flux emitting all tasks.
     */
    public Flux<Task> getAll() {
        log.debug("Service to get all Tasks");
        return repository.findAll();
    }

    /**
     * Create a new task with the provided input.
     *
     * @param input The input containing the task's body.
     * @return A Mono emitting the created task.
     */ 
    public Mono<Task> create(TaskInput input) {
        log.debug("Service to create Task, input {}", input);
        Task task = new Task(null, input.body());
        return this.repository.save(task);
    }

    /**
     * Update an existing task with the provided input.
     *
     * @param input The input containing the task's ID and new body.
     * @return A Mono emitting the updated task.
     */
    public Mono<Task> update(TaskInput input) {
        log.debug("Service to create Task, input {}", input);
        Task task = new Task(input.id(), input.body());
        return this.repository.save(task);
    }
}
