package gamezone.domain.dto.bookings;

import jakarta.validation.constraints.NotBlank;

public record CreatePaymentRequest(
        @NotBlank String method,
        @NotBlank String provider,
        @NotBlank String paymentToken
) {}
