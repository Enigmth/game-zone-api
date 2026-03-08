package gamezone.common.configs;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

// OAuth login (Google) is out of scope for v1.0 — this filter is a no-op placeholder.
@Provider
@Priority(Priorities.AUTHENTICATION + 10)
public class GoogleAuthRequestFilter extends BaseAuthImpl implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Google OAuth not implemented in v1.0
    }
}
