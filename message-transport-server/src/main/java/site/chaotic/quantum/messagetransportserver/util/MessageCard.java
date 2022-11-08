package site.chaotic.quantum.messagetransportserver.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import site.chaotic.quantum.kookframework.enums.MessageType;

@Data
@AllArgsConstructor
public class MessageCard {
    private MessageType type;
    private String channelId;
    private String content;
}
