package site.chaotic.quantum.khlframework.interfaces;

import reactor.core.publisher.Mono;

public interface KHLClient {
    Mono<Void> sendMessage(String channelId, String message);

    Mono<Void> sendImage(String channelId, byte[] data);
}
