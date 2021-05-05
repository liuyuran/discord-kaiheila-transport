package site.chaotic.quantum.khlframework.struct.http;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MessageCreateRequest {
    private Integer type;
    private String targetId;
    private String content;
    private String quote;
    private String nonce;
    private String tempTargetId;
}
