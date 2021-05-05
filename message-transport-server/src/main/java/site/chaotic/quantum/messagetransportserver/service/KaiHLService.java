package site.chaotic.quantum.messagetransportserver.service;

import lombok.Data;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import site.chaotic.quantum.messagetransportserver.config.KHLBotConfig;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

import static site.chaotic.quantum.messagetransportserver.consts.APIConst.*;

/**
 * 开黑啦API服务
 */
@Service
@Log4j2
public class KaiHLService {
    static class WebSocketController implements WebSocketHandler {
        private boolean helloFlag = false;
        private boolean pongFlag = false;
        private int lastSN = 0;
        private String sessionId = "";

        public void reset() {
            helloFlag = false;
            pongFlag = false;
            lastSN = 0;
            sessionId = "";
        }

        @Override
        public Mono<Void> handle(WebSocketSession session) {
            return session.send(session.receive().flatMap(message -> {
                try {
                    String msg = message.getPayloadAsText();
                    log.info("下行指令：" + msg);
                    JSONObject payload = new JSONObject(msg);
                    int msgType = payload.getInt("s");
                    switch (msgType) {
                        case MSG_TYPE_HELLO:
                            helloFlag = true;
                            sessionId = payload.getJSONObject("d").getString("sessionId");
                            return Flux.empty();
                        case MSG_TYPE_PONG:
                            pongFlag = true;
                            return Flux.empty();
                        case MSG_TYPE_RECONNECT:
                            return Mono.error(new Exception("远程服务器要求中断连接"));
                        case MSG_TYPE_API:
                            lastSN = payload.getInt("sn");
                            payload = payload.getJSONObject("d");
                            String channelType = payload.getString("channel_type");
                            int type = payload.getInt("type");
                            switch (channelType) {
                                case "GROUP":
                                    switch (type) {
                                        case 1:
                                            JSONObject obj = new JSONObject();
                                            return Flux.empty();
                                        default:
                                            log.info("暂不支持文字以外的消息");
                                            return Flux.empty();
                                    }
                                case "PERSON":
                                default:
                                    log.info("暂不支持私聊消息");
                                    return Flux.empty();
                            }
                        case MSG_TYPE_RESUME:
                            // 可能是离线消息功能，但目前没想到有什么用
                        default:
                            return Flux.empty();
                    }
                } catch (Exception e) {
                    return Mono.error(e);
                }
            }).mergeWith(Flux.interval(Duration.ofSeconds(1), Duration.ofSeconds(30)).flatMap(item -> {
                // 30秒一次心跳
                try {
                    JSONObject msg = new JSONObject();
                    msg.put("s", MSG_TYPE_PING);
                    msg.put("sn", lastSN);
                    pongFlag = false;
                    return Mono.just(msg);
                } catch (Exception e) {
                    return Mono.error(e);
                }
            })).map(item -> {
                log.info("上行指令：" + item.toString());
                return session.textMessage(item.toString());
            })).mergeWith(Mono.delay(Duration.ofSeconds(6)).flatMap(item -> {
                if (!helloFlag) return Mono.error(new Exception("超过6秒未曾接到hello命令"));
                else return Mono.empty();
            })).mergeWith(Flux.interval(Duration.ofSeconds(7), Duration.ofSeconds(30)).flatMap(item -> {
                if (!pongFlag) return Mono.error(new Exception("超过6秒未曾接到pong命令"));
                else return Mono.empty();
            })).last().doOnError(throwable -> {
                log.info("检测到未捕捉的异常，连接关闭");
                session.close();
            });
        }
    }

    private final WebClient webClient;
    private final WebSocketController controller;
    private final KHLBotConfig botConfig;

    public KaiHLService(WebClient.Builder builder, KHLBotConfig config) {
        webClient = builder.baseUrl(KaiHLBaseUrl).build();
        controller = new WebSocketController();
        botConfig = config;
        connect().subscribe();
    }

    private Mono<String> generateWSGateway() {
        return webClient.get().uri(KaiHLApiGateway)
                .header("Authorization",
                        String.format("Bot %s", botConfig.getToken()))
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
}

@Data
@ToString
class GatewayResponseBody {
    @Data
    static class WssData {
        private String url;
    }

    private Integer code;
    private String message;
    private WssData data;
}
