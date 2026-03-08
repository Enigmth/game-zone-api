package gamezone.services;

import gamezone.domain.dto.ratings.SubmitRatingsRequest;

public class RatingService extends AbstractService {
    public void submitRatings(String gameId, SubmitRatingsRequest request) {
        // TODO enforce completed-game rule and uniqueness constraints.
    }
}
