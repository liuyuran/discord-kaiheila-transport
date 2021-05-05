package site.chaotic.quantum.khlframework.struct.ws;

import lombok.Data;

@Data
public class BaseMessageContent<T> {
    private String channelType;
    private Integer type;
    private String targetId;
    private String authorId;
    private String content;
    private String msgId;
    private Long msgTimestamp;
    private String nonce;
    private T extra;
}
