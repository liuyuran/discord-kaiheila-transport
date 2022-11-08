package site.chaotic.quantum.kookframework;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import site.chaotic.quantum.kookframework.config.KOOKBotConfig;
import site.chaotic.quantum.kookframework.controller.KOOKClientImpl;
import site.chaotic.quantum.kookframework.interfaces.KOOKClient;

@Configuration
@EnableConfigurationProperties(KOOKBotConfig.class)
@ConditionalOnMissingBean(type = "site.chaotic.quantum.kookframework.KOOKClient")
public class KOOKClientAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean({KOOKClient.class})
    public KOOKClient kookClient(WebClient.Builder builder, KOOKBotConfig config,
                                ApplicationContext publisher) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        Gson gson = gsonBuilder.create();
        return new KOOKClientImpl(builder, config, publisher, gson);
    }
}
