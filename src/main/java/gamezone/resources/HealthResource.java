package gamezone.resources;

import gamezone.domain.AbstractResource;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/health")
@Produces(MediaType.APPLICATION_JSON)
public class HealthResource extends AbstractResource {

    @GET
    @Path("/live")
    public Response live() {
        return okWithMessage("ok");
    }

    @GET
    @Path("/ready")
    public Response ready() {
        // TODO add DB/redis connectivity checks.
        return okWithMessage("ready");
    }
}
