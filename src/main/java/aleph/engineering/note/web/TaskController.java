package aleph.engineering.note.web;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import aleph.engineering.note.domain.Task;
import aleph.engineering.note.security.AuthoritiesConstants;
import aleph.engineering.note.services.TaskService;
import aleph.engineering.note.services.dto.TaskInput;
import aleph.engineering.note.web.error.BadRequestAlertException;
import aleph.engineering.note.web.error.ItemNotFoundException;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * GraphQL Controller for managing tasks.
 */
@Controller
public class TaskController {
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);
    private final MessageSource messageSource;
    private final TaskService service;

    public TaskController(TaskService service, MessageSource messageSource) {
        this.messageSource = messageSource;
        this.service = service;
    }

    /**
     * Retrieve a task by ID.
     *
     * @param id The ID of the task to retrieve.
     * @return The task if found.
     * @throws ItemNotFoundException If the task is not found.
     */
    @QueryMapping
    Mono<Task> task(@Argument String id) {
        log.debug("REST request to get Task : {}", id);
        return service.getTask(id).switchIfEmpty(Mono.error(
                new ItemNotFoundException(
                        messageSource.getMessage("task.notfound", new Object[] {id}, LocaleContextHolder.getLocale()))));
    }

    /**
     * Retrieve all tasks.
     *
     * @return A list of all tasks.
     */
    @QueryMapping
    Flux<Task> tasks() {
        log.debug("REST request to getAll Tasks");
        return service.getAll();
    }

    /**
     * Create a new task.
     *
     * @param input The input containing the task's body.
     * @return The created task.
     * @throws BadRequestAlertException If the provided ID is not null.
     */
    @MutationMapping
    Mono<Task> create(@Argument @Valid TaskInput input) {
        log.debug("REST request to create a Task, body {}", input.body());
        if (input.id() != null) {
            throw new BadRequestAlertException(
                    messageSource.getMessage("arguments.null.id" , null,  LocaleContextHolder.getLocale()),
                    "idexists"
                );
        }
        return service.create(input);
    }

     /**
     * Edit an existing task.
     *
     * @param input The input containing the task's ID and new body.
     * @return The edited task.
     * @throws BadRequestAlertException If the provided ID is null.
     */
    @MutationMapping
    Mono<Task> edit(@Argument @Valid TaskInput input) {
        log.debug("REST request to edit a Task, body {}", input.body());
        if (input.id() == null) {
            throw new BadRequestAlertException(
                    messageSource.getMessage("arguments.notnull.id" , null,  LocaleContextHolder.getLocale()), 
                    "idnull"
                );
        }
        return service.update(input);
    }    
}


