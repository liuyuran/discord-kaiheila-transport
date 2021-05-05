package site.chaotic.quantum.khlframework;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import site.chaotic.quantum.khlframework.config.KHLBotConfig;
import site.chaotic.quantum.khlframework.controller.KHLClientImpl;
import site.chaotic.quantum.khlframework.interfaces.KHLClient;

@Configuration
@EnableConfigurationProperties(KHLBotConfig.class)
@ConditionalOnMissingBean(type = "site.chaotic.quantum.khlframework.KhlClient")
public class KhlClientAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean({KHLClient.class})
    public KHLClient khlClient(WebClient.Builder builder, KHLBotConfig config, ApplicationContext publisher) {
        return new KHLClientImpl(builder, config, publisher);
    }
}
