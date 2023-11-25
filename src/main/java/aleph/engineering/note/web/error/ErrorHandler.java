package aleph.engineering.note.web.error;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;

import graphql.GraphQLError;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

/**
 * Controller advice to translate the server side exceptions to client-friendly
 * json structures.
 */
@ControllerAdvice
public class ErrorHandler {

     private final MessageSource messageSource;

     public ErrorHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
     }


    @GraphQlExceptionHandler
    public GraphQLError handleItemNotFoundException(ItemNotFoundException ex, DataFetchingEnvironment env) {
        Locale locale = LocaleContextHolder.getLocale();
        Map<String, Object> problem = new HashMap<>();
        problem.put("title",  messageSource.getMessage("resource.notfound.title", null, locale));
        problem.put("type", ErrorConstants.NOT_FOUND_TYPE);
        problem.put("status", HttpStatus.NOT_FOUND.value());
        problem.put("details", ex.getMessage());

        return GraphQLError.newError()
                .errorType(ErrorType.NOT_FOUND)
                .message(ex.getMessage())
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .extensions(problem)
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleBadRequestException(BadRequestAlertException ex, DataFetchingEnvironment env) {
        Map<String, Object> problem = new HashMap<>();
        problem.put("title",  ex.getMessage());
        problem.put("type", ErrorConstants.CONSTRAINT_VIOLATION_TYPE);
        problem.put("status", HttpStatus.BAD_REQUEST.value());
        problem.put("details", ex.getErrorKey());

        return GraphQLError.newError()
                .errorType(ErrorType.BAD_REQUEST)
                .message(ex.getMessage())
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .extensions(problem)
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleConstraintViolationException(ConstraintViolationException ex, DataFetchingEnvironment env) {
      Locale locale = LocaleContextHolder.getLocale();
      String meessageError = messageSource.getMessage("arguments.no_valid", null, locale);

      Map<String, Object> problem = new HashMap<>();
      problem.put("type", ErrorConstants.CONSTRAINT_VIOLATION_TYPE);
      problem.put("title",  meessageError);
      problem.put("status", HttpStatus.BAD_REQUEST.value());

      List<Map<String, String>> fieldErrors = new ArrayList<>();
      for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
        Map<String, String> fieldError = new HashMap<>();
        fieldError.put("field", violation.getPropertyPath().toString());
        fieldError.put("message", violation.getMessage());
        fieldError.put("code", violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName());
        fieldErrors.add(fieldError);
      }
      problem.put("fieldsErrors", fieldErrors);

        return GraphQLError.newError()
                .errorType(ErrorType.BAD_REQUEST)
                .message(meessageError)
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .extensions(problem)
                .build();
    }
}