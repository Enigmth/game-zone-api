package gamezone.common;

import gamezone.domain.ContextHeaders;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.container.ContainerRequestContext;

public final class AuthenticatedUserResolver {
    private AuthenticatedUserResolver() {
    }

    public static String requireUserId(ContainerRequestContext requestContext) {
        Object userId = requestContext.getProperty(ContextHeaders.AUTH_USER_ID);
        if (userId == null) {
            throw new BadRequestException("Missing authenticated user context");
        }
        return userId.toString();
    }
}
