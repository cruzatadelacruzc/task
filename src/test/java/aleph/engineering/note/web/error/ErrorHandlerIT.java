package aleph.engineering.note.web.error;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.http.HttpStatus;
import aleph.engineering.note.IntegrationTest;

/**
 * Integration tests for the {@link ErrorHandler} Controller Advice.
 */
@IntegrationTest
@AutoConfigureGraphQlTester
public class ErrorHandlerIT {

    @Autowired
    private GraphQlTester graphQlTester;

    @Test
    void testHandleItemNotFoundException() {
        String dummyID = "task-does-not-exist";
        this.graphQlTester.document("{ task(id: \"" + dummyID + "\") { id body } }")
        .execute()
        .errors()
        .expect( error -> {
            String errorMessage = String.format("Task with ID %s not found", dummyID);
             Assertions.assertEquals(ErrorType.NOT_FOUND, error.getErrorType());

             Assertions.assertTrue(Objects.requireNonNullElse(error.getMessage(), "").contains(errorMessage));
 
             Assertions.assertTrue(error.getExtensions() != null);
             Map<String, Object> extensions = error.getExtensions();
             Assertions.assertEquals("Resource Not Found", extensions.get("title"));
             Assertions.assertEquals(ErrorConstants.NOT_FOUND_TYPE, extensions.get("type"));
             Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), extensions.get("status"));
             Assertions.assertEquals(errorMessage, extensions.get("details"));
 
             return true;
        })
        .verify();
    }

    @Test
    void testHandleBadRequestException() {
        this.graphQlTester.document("mutation { create(input: { id: \"1\", body: \"Test Body\" } ) { id body } }")
        .execute()
        .errors()
        .expect(error -> {
            String errorMessage = "ID must be null";
            Assertions.assertEquals(ErrorType.BAD_REQUEST, error.getErrorType());

            Assertions.assertTrue(Objects.requireNonNullElse(error.getMessage(), "").contains(errorMessage));

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
    void testHandleConstraintViolationException() {
        this.graphQlTester.document("mutation { edit(input: { body: \"\" } ) { id body } }")
        .execute()
        .errors()
        .expect(error -> {
            String errorMessage = "Method argument not valid";
            Assertions.assertEquals(ErrorType.BAD_REQUEST, error.getErrorType());

            Assertions.assertTrue(Objects.requireNonNullElse(error.getMessage(), "").contains(errorMessage));

            Assertions.assertTrue(error.getExtensions() != null);
            Map<String, Object> extensions = error.getExtensions();
            Assertions.assertEquals(ErrorConstants.CONSTRAINT_VIOLATION_TYPE, extensions.get("type"));
            Assertions.assertEquals(errorMessage, extensions.get("title"));
            Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), extensions.get("status"));

            Assertions.assertTrue(extensions.containsKey("fieldsErrors"));
            @SuppressWarnings("unchecked")
            List<Map<String, String>> fieldErrors = (List<Map<String, String>>) extensions.get("fieldsErrors");
            Assertions.assertEquals(1, fieldErrors.size());
            Map<String, String> fieldError = fieldErrors.get(0);
            Assertions.assertEquals("NotBlank", fieldError.get("code"));
            Assertions.assertEquals("edit.input.body", fieldError.get("field"));
            Assertions.assertEquals("must not be blank", fieldError.get("message"));

            return true;
        })
        .verify();
    }
}