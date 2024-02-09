package aleph.engineering.note.web;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.graphql.test.tester.GraphQlTester;

import aleph.engineering.note.IntegrationTest;
import aleph.engineering.note.domain.Task;
import aleph.engineering.note.repositories.TaskRepository;
import aleph.engineering.note.security.AuthoritiesConstants;
import aleph.engineering.note.web.error.ErrorConstants;
import reactor.core.publisher.Mono;
import java.util.Map;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * Integration tests for the {@link TaskController} REST controller.
 */
@IntegrationTest
@AutoConfigureGraphQlTester
public class TaskControllerIT {

    
	private final String BODY = "THIS IS A BODY";
    
    @Autowired
    private TaskRepository repository;


	@Autowired
    private GraphQlTester graphQlTester; 

   @Test
   void addTask () {
       Task task = this.graphQlTester
       .document("mutation { create(input: { body: \"" + BODY + "\" }) { id body } }")
       .execute()
       .path("data.create")
       .entity(Task.class)
       .get();
       Assertions.assertNotNull(task);
       Assertions.assertNotNull(task.id());
       Assertions.assertNotNull(task.body());
       Assertions.assertEquals(BODY, task.body());
   }

   @Test
   void editTask () {
       Mono<Task> savedTaskMono = repository.save(new Task(null, BODY));
       Task savedTask = savedTaskMono.block();

       
       String modifiedBody = "Modified Body";

       Task editedTask = this.graphQlTester
       .document("mutation { edit(input: { id: \"" + savedTask.id() + "\", body: \"" + modifiedBody + "\" } ) { id body } }")
       .execute()
       .path("data.edit")
       .entity(Task.class)
       .get();

       Assertions.assertNotNull(editedTask);
       Assertions.assertEquals(savedTask.id(), editedTask.id());
       Assertions.assertEquals(editedTask.body(), modifiedBody);
   }

   @Test
   void getTaskById() {
       Task task = new Task(null, BODY);
       Mono<Task> savedTaskMono = repository.save(task);

       Task savedTask = savedTaskMono.block();

       Task taskResult = this.graphQlTester
               .document("{ task(id: \"" + savedTask.id() + "\") { id body } }")
               .execute()
               .path("data.task")
               .entity(Task.class)
               .get();

       Assertions.assertNotNull(taskResult);
       Assertions.assertNotNull(taskResult.body());
       Assertions.assertEquals(savedTask.id(), taskResult.id());
       Assertions.assertEquals(BODY, taskResult.body());
   }

   @Test
   void getTasks() {
       List<Task> tasks = new ArrayList<>();
       int numberOfTasks = 10;

       for (int i = 0; i < numberOfTasks; i++) {
           Task task = new Task(null, BODY + i);
           tasks.add(task);
       }

       repository.saveAll(tasks).collectList().block();

       List<Task> tasksFromDatabase = repository.findAll().collectList().block();

       List<Task> taskResult = this.graphQlTester
               .document("{ tasks { id body } }")
               .execute()
               .path("data.tasks")
               .entityList(Task.class)
               .hasSize(tasksFromDatabase.size())
               .get();
               
        for (int i = 0; i < tasksFromDatabase.size(); i++) {
            Task taskInDatabase = tasksFromDatabase.get(i);
            Task taskInResult = taskResult.get(i);

            assertEquals(taskInDatabase.id(), taskInResult.id());
            assertEquals(taskInDatabase.body(), taskInResult.body());
        }

   }

    @Test
    void shouldReturnErrorEmptyBodyToCreate () {

        this.graphQlTester.document("mutation { create(input : { body: \"\" }) { id body } }")
        .execute()
        .errors()
        .expect(error -> {           
            String errorMessage = "Method argument not valid";
            Assertions.assertTrue(Objects.requireNonNullElse(error.getMessage(), "").contains(errorMessage));
            Assertions.assertEquals(ErrorType.BAD_REQUEST, error.getErrorType());

            Assertions.assertTrue(error.getExtensions() != null);
            Map<String, Object> extensions = error.getExtensions();
            Assertions.assertEquals(ErrorConstants.CONSTRAINT_VIOLATION_TYPE, extensions.get("type"));
            Assertions.assertEquals(errorMessage, extensions.get("title"));
            Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), extensions.get("status"));

            

            Assertions.assertTrue(extensions.containsKey("fieldsErrors"));
            @SuppressWarnings("unchecked")
            List<Map<String, String>> fieldErrors = error.getExtensions()
            .entrySet().stream()
            .filter(entry -> "fieldsErrors".equals(entry.getKey()) && entry.getValue() instanceof List)
            .findFirst()
            .map(entry -> (List<Map<String, String>>) entry.getValue())
            .orElse(new ArrayList<>());
            Assertions.assertEquals(1, fieldErrors.size());
            Map<String, String> fieldError = fieldErrors.get(0);
            Assertions.assertEquals("NotBlank", fieldError.get("code"));
            Assertions.assertEquals("create.input.body", fieldError.get("field"));
            Assertions.assertEquals("must not be blank", fieldError.get("message"));
            return true;
        })
        .verify();
    }

    @Test
    void shouldReturnIDExistisToCreate () {

        this.graphQlTester.document("mutation { create(input: { id: \"" + 1 + "\", body: \"" + BODY + "\" } ) { id body } }")
        .execute()
        .errors()
        .expect(error -> {       
            String errorMessage = "ID must be null";
            Assertions.assertTrue(Objects.requireNonNullElse(error.getMessage(), "").contains(errorMessage));
            Assertions.assertEquals(ErrorType.BAD_REQUEST, error.getErrorType());

            Assertions.assertTrue(error.getExtensions() != null);
            Map<String, Object> extensions = error.getExtensions();
            Assertions.assertEquals(ErrorConstants.CONSTRAINT_VIOLATION_TYPE, extensions.get("type"));
            Assertions.assertEquals(errorMessage, extensions.get("title"));
            Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), extensions.get("status"));           
            Assertions.assertEquals("idexists", extensions.get("details"));            
            return true;
        })
        .verify();
    }

    @Test
    void shouldReturnItemNotFound () {

        this.graphQlTester.document("{ task(id: \"" + 12 + "\") { id body } }")
        .execute()
        .errors()
        .expect(error -> {       
            String errorMessage = "Task with ID 12 not found";
            Assertions.assertTrue(Objects.requireNonNullElse(error.getMessage(), "").contains(errorMessage));
            Assertions.assertEquals(ErrorType.NOT_FOUND, error.getErrorType());

            Assertions.assertTrue(error.getExtensions() != null);
            Map<String, Object> extensions = error.getExtensions();
            Assertions.assertEquals(ErrorConstants.NOT_FOUND_TYPE, extensions.get("type"));
            Assertions.assertEquals("Resource Not Found", extensions.get("title"));
            Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), extensions.get("status"));           
            Assertions.assertEquals(errorMessage, extensions.get("details"));            
            return true;
        })
        .verify();
    }

    @Test
    void shouldReturnErrorEmptyBodyToEdit () {

        this.graphQlTester.document("mutation { edit(input : { body: \"\" }) { id body } }")
        .execute()
        .errors()
        .expect(error -> {           
            String errorMessage = "Method argument not valid";
            Assertions.assertTrue(Objects.requireNonNullElse(error.getMessage(), "").contains(errorMessage));
            Assertions.assertEquals(ErrorType.BAD_REQUEST, error.getErrorType());

            Assertions.assertTrue(error.getExtensions() != null);
            Map<String, Object> extensions = error.getExtensions();
            Assertions.assertEquals(ErrorConstants.CONSTRAINT_VIOLATION_TYPE, extensions.get("type"));
            Assertions.assertEquals(errorMessage, extensions.get("title"));
            Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), extensions.get("status"));

            

            Assertions.assertTrue(extensions.containsKey("fieldsErrors"));
            @SuppressWarnings("unchecked")
            List<Map<String, String>> fieldErrors = error.getExtensions()
            .entrySet().stream()
            .filter(entry -> "fieldsErrors".equals(entry.getKey()) && entry.getValue() instanceof List)
            .findFirst()
            .map(entry -> (List<Map<String, String>>) entry.getValue())
            .orElse(new ArrayList<>());
            Assertions.assertEquals(1, fieldErrors.size());
            Map<String, String> fieldError = fieldErrors.get(0);
            Assertions.assertEquals("NotBlank", fieldError.get("code"));
            Assertions.assertEquals("edit.input.body", fieldError.get("field"));
            Assertions.assertEquals("must not be blank", fieldError.get("message"));
            return true;
        })
        .verify();
    }

    @Test
    void shouldReturnIDNotExistisToEdit () {

        this.graphQlTester.document("mutation { edit(input: { body: \"" + BODY + "\" } ) { id body } }")
        .execute()
        .errors()
        .expect(error -> {       
            String errorMessage = "ID should not be null";
            Assertions.assertTrue(Objects.requireNonNullElse(error.getMessage(), "").contains(errorMessage));
            Assertions.assertEquals(ErrorType.BAD_REQUEST, error.getErrorType());

            Assertions.assertTrue(error.getExtensions() != null);
            Map<String, Object> extensions = error.getExtensions();
            Assertions.assertEquals(ErrorConstants.CONSTRAINT_VIOLATION_TYPE, extensions.get("type"));
            Assertions.assertEquals(errorMessage, extensions.get("title"));
            Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), extensions.get("status"));           
            Assertions.assertEquals("idnull", extensions.get("details"));            
            return true;
        })
        .verify();
    }
}
