package gamezone.domain.dto.messages;

public record MessageResponse(
        String id,
        String conversationId,
        String senderUserId,
        String text,
        String sentAtUtc
) {}
