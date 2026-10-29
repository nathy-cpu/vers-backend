package org.vers.backend.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import java.util.stream.Collectors;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.vers.backend.dto.ErrorResponse;

public class ExceptionMappers {

    @ServerExceptionMapper
    public Response mapApiException(ApiException e) {
        return Response.status(e.status)
            .entity(new ErrorResponse(e.getMessage()))
            .build();
    }

    // Bean Validation failures raised when an entity is persisted
    @ServerExceptionMapper
    public Response mapConstraintViolation(ConstraintViolationException e) {
        String message = e
            .getConstraintViolations()
            .stream()
            .map(ExceptionMappers::describe)
            .sorted()
            .collect(Collectors.joining("; "));
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(new ErrorResponse(message))
            .build();
    }

    private static String describe(ConstraintViolation<?> violation) {
        return violation.getPropertyPath() + ": " + violation.getMessage();
    }
}
