package site.chaotic.quantum.kookframework.interfaces;

import reactor.core.publisher.Mono;

public interface KOOKClient {
    Mono<Void> sendMessage(String channelId, String message);

    Mono<Void> sendImage(String channelId, byte[] data);
}
