package gamezone.domain.dto.messages;

public record ConversationListItemResponse(
        String id,
        String title,
        String lastMessage,
        String lastMessageAtUtc,
        int unreadCount
) {}
