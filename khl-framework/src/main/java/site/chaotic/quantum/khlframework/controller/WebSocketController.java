package site.chaotic.quantum.khlframework.controller;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import site.chaotic.quantum.khlframework.struct.BaseEvent;
import site.chaotic.quantum.khlframework.struct.ws.NormalMessage;

import java.time.Duration;

import static site.chaotic.quantum.khlframework.config.APIConst.*;

@Log4j2
public class WebSocketController implements WebSocketHandler {
    private final ApplicationContext publisher;

    private final Gson gson = new Gson();

    private boolean helloFlag = false;
    private boolean pongFlag = false;
    private int lastSN = 0;
    private String sessionId = "";

    public WebSocketController(ApplicationContext publisher) {
        this.publisher = publisher;
    }

    public void reset() {
        helloFlag = false;
        pongFlag = false;
        lastSN = 0;
        sessionId = "";
    }

    @SuppressWarnings("NullableProblems")
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
                        // TODO 这里应该还可能是系统消息，所以需要根据type做适配
                        NormalMessage normalMessage = gson.fromJson(msg, NormalMessage.class);
                        if (!normalMessage.getD().getExtra().getAuthor().getBot())
                            publisher.publishEvent(new BaseEvent<>(this, normalMessage.getD()));
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
            // 6秒接不到hello说明要么没连上，要么他们服务器挂了
            if (!helloFlag) return Mono.error(new Exception("超过6秒未曾接到hello命令"));
            else return Mono.empty();
        })).mergeWith(Flux.interval(Duration.ofSeconds(7), Duration.ofSeconds(30)).flatMap(item -> {
            // 6秒接不到回复说明订阅关系断了
            if (!pongFlag) return Mono.error(new Exception("超过6秒未曾接到pong命令"));
            else return Mono.empty();
        })).last().doOnError(throwable -> {
            // 按理说没这么严重需要中断，但1分钟而已，应该没关系
            log.info("检测到未捕捉的异常，连接关闭");
            session.close();
        });
    }
}
