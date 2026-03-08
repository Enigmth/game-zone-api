package gamezone.common.configs;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

// Role-based authorization interceptor — placeholder for v1.0 (single user role).
@Provider
@Priority(Priorities.AUTHORIZATION)
public class Interceptor extends BaseAuthImpl implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Role enforcement not required for v1.0
    }
}
