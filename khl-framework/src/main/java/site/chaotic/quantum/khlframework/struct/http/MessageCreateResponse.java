package site.chaotic.quantum.khlframework.struct.http;

import lombok.Data;

public class MessageCreateResponse extends BaseResponse<MessageCreateResponse.MessageCreate> {
    @Data
    public static class MessageCreate {
        private String msgId;
        private Long msgTimestamp;
        private String nonce;
    }
}
