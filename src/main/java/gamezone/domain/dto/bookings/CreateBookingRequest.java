package gamezone.domain.dto.bookings;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateBookingRequest(
        @NotNull @Min(1) Integer playersToAdd,
        @NotBlank String paymentMethod
) {}
