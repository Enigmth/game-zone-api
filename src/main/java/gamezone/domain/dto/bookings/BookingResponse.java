package gamezone.domain.dto.bookings;

public record BookingResponse(String bookingId, String gameId, String status, int playersBooked) {}
