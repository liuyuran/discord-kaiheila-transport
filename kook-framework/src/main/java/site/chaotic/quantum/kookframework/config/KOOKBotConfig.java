package site.chaotic.quantum.kookframework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import site.chaotic.quantum.kookframework.struct.Command;

import java.util.HashMap;

@ConfigurationProperties(prefix="kook")
@Data
public class KOOKBotConfig {
    private String clientId;
    private String clientSecret;
    private String token;
    private String commandPrefix;
    private HashMap<String, Command> commands;
}
