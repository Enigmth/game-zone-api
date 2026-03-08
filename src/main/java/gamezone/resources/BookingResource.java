package gamezone.resources;

import gamezone.common.AuthenticatedUserResolver;
import gamezone.domain.AbstractResource;
import gamezone.domain.PageResponse;
import gamezone.domain.dto.bookings.BookingResponse;
import gamezone.domain.dto.bookings.CreateBookingRequest;
import gamezone.domain.dto.bookings.CreatePaymentRequest;
import gamezone.services.BookingService;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
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

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BookingResource extends AbstractResource {
    private final BookingService bookingService = new BookingService();

    @POST
    @Path("games/{gameId}/bookings")
    public Response createBooking(
            @Context ContainerRequestContext requestContext,
            @PathParam("gameId") String gameId,
            @Valid CreateBookingRequest request
    ) {
        return toJsonResponse(bookingService.createBooking(AuthenticatedUserResolver.requireUserId(requestContext), gameId, request));
    }

    @POST
    @Path("bookings/{bookingId}/payments")
    public Response createPayment(@PathParam("bookingId") String bookingId, @Valid CreatePaymentRequest request) {
        bookingService.createPayment(bookingId, request);
        return okWithMessage("Payment request accepted");
    }

    @GET
    @Path("users/me/bookings")
    public Response getMyBookings(
            @Context ContainerRequestContext requestContext,
            @QueryParam("status") String status,
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size
    ) {
        int normalizedPage = page == null ? 0 : Math.max(page, 0);
        int normalizedSize = size == null ? 20 : Math.max(size, 1);
        return toJsonResponse(bookingService.getMyBookings(AuthenticatedUserResolver.requireUserId(requestContext), status, normalizedPage, normalizedSize));
    }

    @DELETE
    @Path("bookings/{bookingId}")
    public Response cancelBooking(@Context ContainerRequestContext requestContext, @PathParam("bookingId") String bookingId) {
        bookingService.cancelBooking(AuthenticatedUserResolver.requireUserId(requestContext), bookingId);
        return okWithMessage("Booking cancelled successfully");
    }
}
