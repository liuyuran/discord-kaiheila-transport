package site.chaotic.quantum.messagetransportserver.service;

import discord4j.common.ReactorResources;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.discordjson.json.MessageData;
import discord4j.rest.request.RouterOptions;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;
import site.chaotic.quantum.messagetransportserver.config.DiscordBotConfig;
import site.chaotic.quantum.messagetransportserver.consts.MessageType;
import site.chaotic.quantum.messagetransportserver.util.MessageCard;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class DiscordService {
    private final BridgeService bridgeService;
    private final DiscordClient client;

    public <T extends Event> DiscordService(DiscordBotConfig botConfig, BridgeService bridgeService) {
        DiscordClientBuilder<DiscordClient, RouterOptions> builder = DiscordClientBuilder.create(botConfig.getToken());
        if (botConfig.getWithLocalProxy()) {
            ReactorResources reactorResources = ReactorResources.builder()
                    .httpClient(HttpClient.create().proxy(options -> {
                        options.type(ProxyProvider.Proxy.SOCKS5)
                                .host("127.0.0.1")
                                .port(10808)
                                .build();
                    }))
                    .build();
            builder.setReactorResources(reactorResources);
        }
        client = builder.build();
        client.withGateway(gateway -> {
            final Publisher<?> pingPong = gateway.on(MessageCreateEvent.class, event ->
                    Mono.just(event.getMessage())
                            .flatMap(message -> message.getChannel().flatMap(channel -> {
                                log.info(String.format("[%s] %s", channel.getId().asString(), message.getData().content()));
                                if (message.getUserData().bot().toOptional().orElse(false)) return Mono.empty();
                                bridgeService.addToKook(new MessageCard(
                                        MessageType.Text,
                                        channel.getId().asString(),
                                        String.format("%s: %s",
                                                message.getUserData().username(),
                                                message.getData().content())
                                ));
                                return Mono.empty();
                            })));

            final Publisher<?> onDisconnect = gateway.onDisconnect()
                    .doOnTerminate(() -> log.info("Discord disconnected!"));

            return Mono.when(pingPong, onDisconnect);
        }).subscribe();
        this.bridgeService = bridgeService;
    }

    @Scheduled(cron = "*/10 * * * * ? ")
    public void syncMessage() {
        client.withGateway(gateway -> gateway.getSelf().flatMap(user -> {
            List<MessageCard> messageCards = bridgeService.clearToDiscord();
            List<Mono<MessageData>> task = new ArrayList<>();
            for (MessageCard card : messageCards) {
                if (bridgeService.translateChannelId(card.getChannelId()) == null) continue;
                switch (card.getType()) {
                    case Text:
                        task.add(gateway.getChannelById(Snowflake.of(
                                bridgeService.translateChannelId(card.getChannelId())
                        )).flatMap(channel -> channel.getRestChannel().createMessage(card.getContent())));
                        break;
                    case Image:
                    case Unknown:
                    default:
                        break;
                }
            }
            return Mono.zip(task, objects -> objects);
        })).subscribe();
    }
}
