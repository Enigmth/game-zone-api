package gamezone.resources;

import gamezone.common.AuthenticatedUserResolver;
import gamezone.domain.AbstractResource;
import gamezone.domain.dto.users.PatchUserProfileRequest;
import gamezone.services.UserService;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource extends AbstractResource {
    private final UserService userService = new UserService();

    @GET
    @Path("/me")
    public Response getMe(@Context ContainerRequestContext requestContext) {
        String userId = AuthenticatedUserResolver.requireUserId(requestContext);
        return toJsonResponse(userService.getMe(userId));
    }

    @PATCH
    @Path("/me")
    public Response patchMe(
            @Context ContainerRequestContext requestContext,
            @Valid PatchUserProfileRequest request
    ) {
        String userId = AuthenticatedUserResolver.requireUserId(requestContext);
        return toJsonResponse(userService.patchMe(userId, request));
    }

    @GET
    @Path("/me/friends")
    public Response getMyFriends(
            @Context ContainerRequestContext requestContext,
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size
    ) {
        String userId = AuthenticatedUserResolver.requireUserId(requestContext);
        int normalizedPage = page == null ? 0 : Math.max(page, 0);
        int normalizedSize = size == null ? 20 : Math.max(size, 1);
        return toJsonResponse(userService.getMyFriends(userId, normalizedPage, normalizedSize));
    }
}
