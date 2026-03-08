package gamezone.domain.dto.ratings;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record RatingItemRequest(
        @NotBlank String ratedUserId,
        @Min(1) @Max(5) int stars
) {}
