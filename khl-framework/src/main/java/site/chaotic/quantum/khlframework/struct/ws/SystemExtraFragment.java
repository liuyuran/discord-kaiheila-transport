package site.chaotic.quantum.khlframework.struct.ws;

import lombok.Data;

@Data
public class SystemExtraFragment<T> {
    private String type;
    private T body;
}
