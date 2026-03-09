package gamezone.resources;

import gamezone.domain.AbstractResource;
import gamezone.domain.dto.auth.AuthTokensResponse;
import gamezone.domain.dto.auth.LoginRequest;
import gamezone.domain.dto.auth.PhoneRequestRequest;
import gamezone.domain.dto.auth.PhoneVerifyRequest;
import gamezone.domain.dto.auth.RefreshRequest;
import gamezone.domain.dto.auth.RegisterRequest;
import gamezone.services.AuthService;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource extends AbstractResource {
    private final AuthService authService = new AuthService();

    @POST
    @Path("/register")
    public Response register(@Valid RegisterRequest request) {
        return toJsonResponse(authService.register(request));
    }

    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest request) {
        return toJsonResponse(authService.login(request));
    }

    @POST
    @Path("/refresh")
    public Response refresh(@Valid RefreshRequest request) {
        return toJsonResponse(authService.refresh(request));
    }

    @POST
    @Path("/logout")
    public Response logout(@Valid RefreshRequest request) {
        authService.logout(request.refreshToken());
        return okWithMessage("Logged out successfully");
    }

    @POST
    @Path("/phone/request")
    public Response phoneRequest(@Valid PhoneRequestRequest request) {
        authService.requestPhoneCode(request);
        return okWithMessage("Code sent");
    }

    @POST
    @Path("/phone/verify")
    public Response phoneVerify(@Valid PhoneVerifyRequest request) {
        return toJsonResponse(authService.verifyPhoneCode(request));
    }
}
