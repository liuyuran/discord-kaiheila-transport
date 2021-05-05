package site.chaotic.quantum.khlframework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="khl")
@Data
public class KHLBotConfig {
    private String token;
}
