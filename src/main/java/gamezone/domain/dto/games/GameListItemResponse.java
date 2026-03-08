package gamezone.domain.dto.games;

public record GameListItemResponse(
        String id,
        String title,
        String status,
        String organizerName,
        String locationLabel,
        String levelLabel,
        int spotsLeft,
        String timeLabel,
        String priceLabel,
        String imageUrl
) {}
