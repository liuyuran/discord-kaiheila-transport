package site.chaotic.quantum.khlframework.event;

import org.springframework.context.ApplicationEvent;

public class BaseEvent<T> extends ApplicationEvent {
    private final T data;

    public BaseEvent(Object source, T data) {
        super(source);
        this.data = data;
    }
}
