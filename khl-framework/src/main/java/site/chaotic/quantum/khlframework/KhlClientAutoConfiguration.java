package site.chaotic.quantum.khlframework;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import site.chaotic.quantum.khlframework.config.KHLBotConfig;

@Configuration
@EnableConfigurationProperties(KHLBotConfig.class)
@ConditionalOnMissingBean(type = "site.chaotic.quantum.khlframework.KhlClientAutoConfiguration")
public class KhlClientAutoConfiguration {

}
