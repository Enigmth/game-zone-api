package gamezone.domain.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PhoneRequestRequest(
        @NotBlank
        @Size(min = 7, max = 20)
        @Pattern(regexp = "\\+?[0-9]{7,19}", message = "Invalid phone number")
        String phone
) {}
