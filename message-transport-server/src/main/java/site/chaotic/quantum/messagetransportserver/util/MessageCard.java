package site.chaotic.quantum.messagetransportserver.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageCard {
    private String channelId;
    private String content;
}
