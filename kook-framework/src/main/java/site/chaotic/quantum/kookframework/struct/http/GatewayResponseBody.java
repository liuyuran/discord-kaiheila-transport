package site.chaotic.quantum.kookframework.struct.http;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GatewayResponseBody {
    @Data
    public static class WssData {
        private String url;
    }

    private Integer code;
    private String message;
    private WssData data;
}
