package gamezone.domain.dto.ratings;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record SubmitRatingsRequest(
        @NotEmpty List<@Valid RatingItemRequest> ratings
) {}
