package site.chaotic.quantum.kookframework.interfaces;

import reactor.core.publisher.Mono;

/**
 * HTTP API接口的抽象，这部分功能无法通过websocket实现
 */
public interface KOOKClient {
    /**
     * 发送纯文本信息
     *
     * @param channelId 目标频道
     * @param message   文本
     * @return 对该操作的异步回调
     */
    Mono<Void> sendMessage(String channelId, String message);

    /**
     * 发送纯图片信息
     *
     * @param channelId 目标频道
     * @param data      图片内容
     * @return 对该操作的异步回调
     */
    Mono<Void> sendImage(String channelId, byte[] data);
}
