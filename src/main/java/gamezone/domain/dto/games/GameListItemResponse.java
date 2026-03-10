package gamezone.domain.dto.games;

public record GameListItemResponse(
        String id,
        String title,
        String location,
        String date,
        int durationMinutes,
        int maxPlayers,
        String sport,
        boolean isFree,
        String price,
        boolean isPublic,
        String shareLink,
        String status,
        String organizerId,
        String teamAColor,
        String teamBColor
) {}
