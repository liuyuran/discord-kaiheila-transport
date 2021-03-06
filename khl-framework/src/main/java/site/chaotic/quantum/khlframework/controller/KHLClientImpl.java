package site.chaotic.quantum.khlframework.controller;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;
import site.chaotic.quantum.khlframework.config.KHLBotConfig;
import site.chaotic.quantum.khlframework.interfaces.KHLClient;
import site.chaotic.quantum.khlframework.json.GsonDecoder;
import site.chaotic.quantum.khlframework.json.GsonEncoder;
import site.chaotic.quantum.khlframework.struct.http.GatewayResponseBody;
import site.chaotic.quantum.khlframework.struct.http.MessageCreateRequest;
import site.chaotic.quantum.khlframework.struct.http.MessageCreateResponse;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

import static site.chaotic.quantum.khlframework.config.APIConst.*;

@Log4j2
public class KHLClientImpl implements KHLClient {
    private final WebClient webClient;
    private final WebSocketController controller;
    private final KHLBotConfig botConfig;

    public KHLClientImpl(WebClient.Builder builder,
                         KHLBotConfig config,
                         ApplicationContext publisher, Gson gson) {
        webClient = builder.baseUrl(KaiHLBaseUrl).codecs(clientCodecConfigurer -> {
            clientCodecConfigurer.customCodecs().register(new GsonEncoder(gson));
            clientCodecConfigurer.customCodecs().register(new GsonDecoder(gson));
        }).build();
        controller = new WebSocketController(publisher, gson);
        botConfig = config;
        connect().subscribe();
    }

    private Mono<String> generateWSGateway() {
        return webClient.get().uri(KaiHLApiGateway)
                .header("Authorization",
                        String.format("Bot %s", botConfig.getToken()))
                .retrieve().bodyToMono(GatewayResponseBody.class)
                .flatMap(item -> {
                    log.info("gateway??????????????????" + item.toString());
                    if (item.getCode() != 0)
                        return Mono.error(new Exception(item.getMessage()));
                    return Mono.just(item.getData().getUrl());
                });
    }

    private Mono<Void> connect() {
        // ????????????????????????60???
        log.info("websocket????????????");
        WebSocketClient client = new ReactorNettyWebSocketClient();
        controller.reset();
        return generateWSGateway().flatMap(url -> {
            log.info("??????????????????" + url);
            try {
                return client.execute(new URI(url), controller)
                        .onErrorResume(throwable -> {
                            log.info("????????????????????????????????????60????????????");
                            return Mono.delay(Duration.ofSeconds(60))
                                    .flatMap(item -> {
                                        log.info("????????????");
                                        return connect();
                                    });
                        });
            } catch (URISyntaxException e) {
                return Mono.error(e);
            }
        }).doOnTerminate(() -> {
            log.info("?????????????????????????????????");
            connect().subscribe();
        });
    }

    @Override
    public Mono<Void> sendMessage(String channelId, String content) {
        return webClient.post().uri(KaiHLApiMessageCreate)
                .header("Authorization", String.format("Bot %s", botConfig.getToken()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(MessageCreateRequest.builder()
                        .type(1)
                        .targetId(channelId)
                        .content(content)
                        .build()), MessageCreateRequest.class)
                .retrieve().bodyToMono(MessageCreateResponse.class).flatMap(item -> {
                    if (item.getCode() == 0) return Mono.empty();
                    return Mono.error(new Exception(item.getMessage()));
                });
    }
}
