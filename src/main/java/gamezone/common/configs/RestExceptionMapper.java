package gamezone.common.configs;

import com.google.gson.JsonSyntaxException;
import gamezone.domain.Responses;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ResourceContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.io.FileNotFoundException;
import java.nio.file.NoSuchFileException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.text.ParseException;

@Provider
public class RestExceptionMapper implements ExceptionMapper<Exception> {

    @Context
    ResourceContext resourceContext;

    @Override
    public Response toResponse(Exception exception) {
        return toResponse(exception, resourceContext.getResource(ContainerRequestContext.class));
    }

    public Response toResponse(Throwable exception, ContainerRequestContext requestContext) {
        Response response = handleBadRequest(exception, requestContext);
        if (response != null) {
            return logError(response, exception, requestContext);
        }
        response = handleSyntaxErrors(exception, requestContext);
        if (response != null) {
            return logError(response, exception, requestContext);
        }

        response = handleSQLException(exception, requestContext);
        if (response != null) {
            return logError(response, exception, requestContext);
        }

        response = handleNotFoundException(exception, requestContext);
        if (response != null) {
            return logError(response, exception, requestContext);
        }

        if (exception instanceof ClientErrorException) {
            return logError(((ClientErrorException) exception).getResponse(), exception, requestContext);
        }
        return logError(Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(Response.Status.SERVICE_UNAVAILABLE.getReasonPhrase()).build(), exception, requestContext);
    }

    private Response logError(Response response, Throwable exception, ContainerRequestContext requestContext) {
        boolean forceLog = false;
        String msg = "caught exception";
        logError(exception, msg, forceLog);
        return response;
    }

    private boolean shouldSendToSentry(Response response, Throwable exception) {
        if (response == null || exception == null) {
            return false;
        }
        return response.getStatus() >= 500;
    }

    private void logError(Throwable exception, String message, Boolean forceLog) {
        System.out.println(message + exception);
    }

    private Response handleBadRequest(Throwable exception, ContainerRequestContext requestContext) {
        System.out.println("handleBadRequest");

        if (exception.getClass().isAssignableFrom(BadRequestException.class)) {
            return new Responses().badRequest(exception.getMessage(), new BadRequestException(exception).getResponse().getStatus());
        }

        if (exception.getClass().isAssignableFrom(IllegalStateException.class)) {
            return new Responses().badRequest(exception.getMessage(), 400);

        }

//        if (exception.getClass().isAssignableFrom(BadRequestWithJSONException.class)) {
//            return apiResponseHandler.handle(requestContext, null, exception,
//                    new Responses().badRequestWithJSON(exception.getMessage(), ((BadRequestWithJSONException) exception).getStatusCode()),
//                    ((BadRequestWithJSONException) exception).getStatusCode());
//        }

        return null;
    }

    private Response handleSyntaxErrors(Throwable exception, ContainerRequestContext requestContext) {
        if (exception.getClass().isAssignableFrom(JsonSyntaxException.class) || exception.getClass().isAssignableFrom(NumberFormatException.class)
                || exception.getClass().isAssignableFrom(ParseException.class)
                || exception.getClass().isAssignableFrom(IllegalArgumentException.class)) {
            return new Responses().badRequest(exception.getMessage(), 400);

        }
        return null;
    }

    private Response handleSQLException(Throwable exception, ContainerRequestContext requestContext) {
        if ((exception.getCause() != null && (exception.getCause()
                instanceof SQLIntegrityConstraintViolationException ||
                exception.getCause() instanceof SQLIntegrityConstraintViolationException)) ||
                exception instanceof SQLIntegrityConstraintViolationException) {
            return new Responses().badRequest("Duplicate Data Exception", 400);
        }

        if (exception instanceof SQLSyntaxErrorException ||
                exception instanceof IllegalStateException) {
            return new Responses().badRequest("internal server error", 500);
        }

        return null;
    }

    private Response handleNotFoundException(Throwable exception, ContainerRequestContext requestContext) {
        if (exception.getClass().isAssignableFrom(NotFoundException.class)
                || exception.getClass().isAssignableFrom(FileNotFoundException.class)
                || exception.getClass().isAssignableFrom(NoSuchFileException.class)) {
            return new Responses().notFound();
        }
        return null;
    }

}
