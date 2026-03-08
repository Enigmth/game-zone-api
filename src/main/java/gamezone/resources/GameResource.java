package gamezone.resources;

import gamezone.common.AuthenticatedUserResolver;
import gamezone.domain.AbstractResource;
import gamezone.domain.PageResponse;
import gamezone.domain.dto.games.CreateGameRequest;
import gamezone.domain.dto.games.CreateGameResponse;
import gamezone.domain.dto.games.GameDetailResponse;
import gamezone.domain.dto.games.GameListItemResponse;
import gamezone.domain.dto.games.ParticipantResponse;
import gamezone.domain.dto.ratings.SubmitRatingsRequest;
import gamezone.services.GameService;
import gamezone.services.RatingService;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/games")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GameResource extends AbstractResource {
    private final GameService gameService = new GameService();
    private final RatingService ratingService = new RatingService();

    @GET
    public Response getGames(
            @QueryParam("date") String date,
            @QueryParam("search") String search,
            @QueryParam("level") String level,
            @QueryParam("visibility") String visibility,
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size
    ) {
        int normalizedPage = page == null ? 0 : Math.max(page, 0);
        int normalizedSize = size == null ? 20 : Math.max(size, 1);
        return toJsonResponse(gameService.getGames(normalizedPage, normalizedSize));
    }

    @GET
    @Path("/{gameId}")
    public Response getGameById(@PathParam("gameId") String gameId) {
        return toJsonResponse(gameService.getGameById(gameId));
    }

    @POST
    public Response createGame(@Context ContainerRequestContext requestContext, @Valid CreateGameRequest request) {
        return toJsonResponse(gameService.createGame(AuthenticatedUserResolver.requireUserId(requestContext), request));
    }

    @GET
    @Path("/{gameId}/participants")
    public Response getParticipants(@PathParam("gameId") String gameId) {
        return toJsonResponse(gameService.getParticipants(gameId));
    }

    @POST
    @Path("/{gameId}/ratings")
    public Response submitRatings(@PathParam("gameId") String gameId, @Valid SubmitRatingsRequest request) {
        ratingService.submitRatings(gameId, request);
        return okWithMessage("Ratings submitted successfully");
    }
}
