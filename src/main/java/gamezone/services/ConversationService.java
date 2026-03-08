package gamezone.services;

import gamezone.domain.PageResponse;
import gamezone.domain.dto.messages.ConversationListItemResponse;
import gamezone.domain.dto.messages.MessageResponse;
import gamezone.domain.dto.messages.SendMessageRequest;

import java.util.List;

public class ConversationService extends AbstractService {
    public PageResponse<ConversationListItemResponse> getMyConversations(int page, int size) {
        List<ConversationListItemResponse> items = List.of(
                new ConversationListItemResponse(
                        "cccccccc-cccc-cccc-cccc-ccccccccccc1",
                        "Alex & Maria",
                        "Yes, I will be there in 20 minutes.",
                        "2026-03-01T18:30:00Z",
                        1
                ),
                new ConversationListItemResponse(
                        "cccccccc-cccc-cccc-cccc-ccccccccccc2",
                        "Saturday Football Group",
                        "Great match everyone.",
                        "2026-03-01T20:10:00Z",
                        0
                )
        );
        return new PageResponse<>(items, page, size, 2, 1);
    }

    public List<MessageResponse> getMessages(String conversationId, String before, int size) {
        return List.of(
                new MessageResponse(
                        "dddddddd-dddd-dddd-dddd-ddddddddddd1",
                        conversationId,
                        "11111111-1111-1111-1111-111111111111",
                        "Hey, are you joining tonight?",
                        "2026-03-01T18:15:00Z"
                ),
                new MessageResponse(
                        "dddddddd-dddd-dddd-dddd-ddddddddddd2",
                        conversationId,
                        "22222222-2222-2222-2222-222222222222",
                        "Yes, I will be there in 20 minutes.",
                        "2026-03-01T18:30:00Z"
                )
        );
    }

    public MessageResponse sendMessage(String conversationId, String senderUserId, SendMessageRequest request) {
        return new MessageResponse("msg-id", conversationId, senderUserId, request.text(), java.time.Instant.now().toString());
    }
}
