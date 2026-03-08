package gamezone.domain;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import gamezone.services.CommonServiceImpl;

import java.util.List;

public class Responses {

    public Response badRequest() {
        return badRequest("Bad request");
    }

    public Response jsonWithStatusCode(String entity, int statusCode) {
        return Response.status(statusCode)
                .entity(entity)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }

    public Response serviceUnavailable() {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(Response.Status.SERVICE_UNAVAILABLE.getReasonPhrase())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }

    public Response badRequestWithJSON(String entity) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(entity)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }

    public Response badRequestWithJSON(String entity, int statusCode) {
        return Response.status(statusCode)
                .entity(entity)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    public Response badRequest(String message) {
        message = message == null ? "" : message;
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(String.format(ResponseMessages.MESSAGE, message))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    public Response badRequest(String message, int statusCode) {
        message = message == null ? "" : message;
        return Response.status(statusCode)
                .entity(String.format(ResponseMessages.MESSAGE, message))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    public Response forbidden() {
        return Response.status(Response.Status.FORBIDDEN)
                .entity("Forbidden")
                .build();
    }

    public Response notFound(String responseMessage) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(String.format(ResponseMessages.MESSAGE, responseMessage))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    public Response notFound() {
        return Response.status(Response.Status.NOT_FOUND)
                .entity("Not found")
                .build();
    }

    public Response ok(String entity) {
        return Response.status(Response.Status.OK)
                .entity(entity)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    public Response ok(int code, String entity) {
        return Response.status(code)
                .entity(entity)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    public Response ok(List<?> list) {
        return ok(CommonServiceImpl.getInstance().gson().toJson(list));
    }

    public Response okWithMessage(String message) {
        return Response.status(Response.Status.OK)
                .entity(String.format(ResponseMessages.MESSAGE, message))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    public Response notSaved(String entity) {
        return Response.status(Response.Status.NOT_MODIFIED)
                .entity(String.format(ResponseMessages.MESSAGE, entity))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }


    public Response updated() {
        return okWithMessage("Updated");
    }

    public Response updated(String responseMessage) {
        return ok(responseMessage);
    }

    public Response deleted() {
        return okWithMessage("Deleted");
    }

    public Response created(String responseMessage) {
        return Response.status(Response.Status.CREATED)
                .entity(responseMessage)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}
