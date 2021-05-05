package site.chaotic.quantum.messagetransportserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="discord")
@Data
public class DiscordBotConfig {
    private String token;
}
