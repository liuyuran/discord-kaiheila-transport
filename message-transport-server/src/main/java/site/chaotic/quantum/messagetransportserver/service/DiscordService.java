package site.chaotic.quantum.messagetransportserver.service;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.rest.request.RouterOptions;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import site.chaotic.quantum.messagetransportserver.config.DiscordBotConfig;

import java.util.List;

@Service
public class DiscordService {
//    private final DiscordClient client;

    public <T extends Event> DiscordService(DiscordBotConfig botConfig) {
        /*client = DiscordClient.create(botConfig.getToken());
        client.withGateway(gateway -> {
            final Publisher<?> pingPong = gateway.on(MessageCreateEvent.class, event ->
                    Mono.just(event.getMessage())
                            .flatMap(message -> message.getChannel().flatMap(channel -> {
                               return Mono.empty();
                            })));

            final Publisher<?> onDisconnect = gateway.onDisconnect()
                    .doOnTerminate(() -> System.out.println("Disconnected!"));

            return Mono.when(pingPong, onDisconnect);
        }).block();*/
    }
}
