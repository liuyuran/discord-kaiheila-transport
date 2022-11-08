package site.chaotic.quantum.kookframework.enums;

import java.util.HashMap;

/**
 * KOOK的消息类型映射
 * <p>目前的基本原则就是用到一种就添加一种</p>
 */
public enum MessageType {
    Unknown(-1),
    Image(2),
    Text(9);

    private static final HashMap<Integer, MessageType> reverseKookLink = new HashMap<>();
    static {
        for (MessageType type: MessageType.values()) {
            reverseKookLink.put(type.getKookType(), type);
        }
    }
    private final int kookType;

    MessageType(int kookType) {
        this.kookType = kookType;
    }

    public int getKookType() {
        return kookType;
    }

    public static MessageType fromKookCode(int code) {
        return reverseKookLink.getOrDefault(code, Unknown);
    }
}
