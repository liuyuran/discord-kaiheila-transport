package site.chaotic.quantum.khlframework.struct.http;

import lombok.Data;

@Data
public class BaseResponse<T> {
    private Integer code;
    private String message;
    private T data;
}
