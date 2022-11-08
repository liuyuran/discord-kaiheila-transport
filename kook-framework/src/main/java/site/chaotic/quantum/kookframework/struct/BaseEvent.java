package site.chaotic.quantum.kookframework.struct;

import org.springframework.context.ApplicationEvent;

/**
 * 利用Spring Boot内部的消息总线机制传递网络层消息所用的结构
 *
 * @param <T> 消息的可变结构
 */
public class BaseEvent<T> extends ApplicationEvent {
    private final T data;

    public BaseEvent(Object source, T data) {
        super(source);
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
