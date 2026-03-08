package gamezone.domain.dto.users;

public record UserProfileResponse(
        String id,
        String fullName,
        String avatarUrl,
        double ratingAverage,
        long ratingCount,
        String preferredPosition,
        long matchesPlayed,
        long hoursPlayed,
        long facilitiesPlayed,
        long winStreak
) {}
