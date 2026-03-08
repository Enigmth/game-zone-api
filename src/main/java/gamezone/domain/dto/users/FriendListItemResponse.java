package gamezone.domain.dto.users;

public record FriendListItemResponse(
        String id,
        String name,
        String avatarUrl,
        int gamesTogether,
        int hoursTogether,
        double rating,
        int wins,
        int losses,
        int winStreak
) {}
