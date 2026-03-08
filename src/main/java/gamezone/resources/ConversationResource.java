package gamezone.resources;

import gamezone.domain.AbstractResource;
import gamezone.domain.PageResponse;
import gamezone.domain.dto.messages.ConversationListItemResponse;
import gamezone.domain.dto.messages.MessageResponse;
import gamezone.domain.dto.messages.SendMessageRequest;
import gamezone.services.ConversationService;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConversationResource extends AbstractResource {
    private final ConversationService conversationService = new ConversationService();

    @GET
    @Path("users/me/conversations")
    public Response getMyConversations(
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size
    ) {
        int normalizedPage = page == null ? 0 : Math.max(page, 0);
        int normalizedSize = size == null ? 20 : Math.max(size, 1);
        return toJsonResponse(conversationService.getMyConversations(normalizedPage, normalizedSize));
    }

    @GET
    @Path("conversations/{conversationId}/messages")
    public Response getMessages(
            @PathParam("conversationId") String conversationId,
            @QueryParam("before") String before,
            @QueryParam("size") Integer size
    ) {
        int normalizedSize = size == null ? 30 : Math.max(size, 1);
        return toJsonResponse(conversationService.getMessages(conversationId, before, normalizedSize));
    }

    @POST
    @Path("conversations/{conversationId}/messages")
    public Response sendMessage(
            @PathParam("conversationId") String conversationId,
            @Valid SendMessageRequest request
    ) {
        // TODO replace mock user with user from JWT principal.
        return toJsonResponse(conversationService.sendMessage(conversationId, "me", request));
    }
}
