package site.chaotic.quantum.khlframework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="kook")
@Data
public class KHLBotConfig {
    private String clientId;
    private String clientSecret;
    private String token;
    private String commandPrefix;
}
