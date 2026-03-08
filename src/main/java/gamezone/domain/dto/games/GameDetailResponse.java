package gamezone.domain.dto.games;

import java.util.List;

public record GameDetailResponse(
        String id,
        String title,
        String status,
        String organizerName,
        String location,
        String level,
        int capacity,
        int spotsLeft,
        String startsAtUtc,
        Integer priceCents,
        boolean isPublic,
        List<String> friendsGoing
) {}
