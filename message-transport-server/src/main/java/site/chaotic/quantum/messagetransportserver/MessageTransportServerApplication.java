package site.chaotic.quantum.messagetransportserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import site.chaotic.quantum.messagetransportserver.config.DiscordBotConfig;
import site.chaotic.quantum.messagetransportserver.config.KHLBotConfig;

@SpringBootApplication
@EnableScheduling
public class MessageTransportServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageTransportServerApplication.class, args);
    }

}
