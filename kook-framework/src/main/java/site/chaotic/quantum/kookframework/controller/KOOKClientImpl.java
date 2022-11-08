package site.chaotic.quantum.kookframework.controller;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;
import site.chaotic.quantum.kookframework.config.KOOKBotConfig;
import site.chaotic.quantum.kookframework.interfaces.KOOKClient;
import site.chaotic.quantum.kookframework.json.GsonDecoder;
import site.chaotic.quantum.kookframework.json.GsonEncoder;
import site.chaotic.quantum.kookframework.struct.http.AssetCreateResponse;
import site.chaotic.quantum.kookframework.struct.http.GatewayResponseBody;
import site.chaotic.quantum.kookframework.struct.http.MessageCreateRequest;
import site.chaotic.quantum.kookframework.struct.http.MessageCreateResponse;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

import static site.chaotic.quantum.kookframework.config.APIConst.*;

@Log4j2
public class KOOKClientImpl implements KOOKClient {
    private final WebClient webClient;
    private final WebSocketController controller;
    private final KOOKBotConfig botConfig;

    public KOOKClientImpl(WebClient.Builder builder,
                          KOOKBotConfig config,
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
                .header("Authorization", String.format("Bot %s", botConfig.getToken()))
                .retrieve().bodyToMono(GatewayResponseBody.class)
                .flatMap(item -> {
                    log.info("gateway接口返回值：" + item.toString());
                    if (item.getCode() != 0)
                        return Mono.error(new Exception(item.getMessage()));
                    return Mono.just(item.getData().getUrl());
                });
    }

    private Mono<Void> connect() {
        // 两次尝试时间间隔60秒
        log.info("websocket准备启动");
        WebSocketClient client = new ReactorNettyWebSocketClient();
        controller.reset();
        return generateWSGateway().flatMap(url -> {
            log.info("地址已获取：" + url);
            try {
                return client.execute(new URI(url), controller)
                        .onErrorResume(throwable -> {
                            log.info("检测到未捕捉的异常，延迟60秒后重连");
                            return Mono.delay(Duration.ofSeconds(60))
                                    .flatMap(item -> {
                                        log.info("开始重连");
                                        return connect();
                                    });
                        });
            } catch (URISyntaxException e) {
                return Mono.error(e);
            }
        }).doOnTerminate(() -> {
            log.info("连接中断，正在自动重连");
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

    @Override
    public Mono<Void> sendImage(String channelId, byte[] data) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new ByteArrayResource(data)).filename("anonymous.png");
        return webClient.post().uri(KaiHLApiAssetCreate)
                .header("Authorization", String.format("Bot %s", botConfig.getToken()))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve().bodyToMono(AssetCreateResponse.class).flatMap(item -> {
                    if (item.getCode() == 0) return Mono.just(item.getData().getUrl());
                    return Mono.error(new Exception(item.getMessage()));
                }).flatMap(url -> sendMessage(channelId, url));
    }
}
