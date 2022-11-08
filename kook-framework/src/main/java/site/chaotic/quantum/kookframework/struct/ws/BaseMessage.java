package site.chaotic.quantum.kookframework.struct.ws;

import lombok.Data;

@Data
public class BaseMessage<T> {
    private Integer s;
    private BaseMessageContent<T> d;
}
