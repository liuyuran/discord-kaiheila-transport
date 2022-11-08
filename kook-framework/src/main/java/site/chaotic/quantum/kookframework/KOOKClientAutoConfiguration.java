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
import site.chaotic.quantum.kookframework.listener.MessageListener;
import site.chaotic.quantum.kookframework.struct.Command;

import java.util.List;

/**
 * spring boot starter的起始注入类，由resources/META_INF/spring.factories发起调用
 */
@Configuration
@EnableConfigurationProperties(KOOKBotConfig.class)
@ConditionalOnMissingBean(type = "site.chaotic.quantum.kookframework.KOOKClient")
public class KOOKClientAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean({KOOKClient.class})
    public KOOKClient kookClient(WebClient.Builder builder, KOOKBotConfig config,
                                ApplicationContext publisher) {
        // 这是与KOOK进行websocket交互时所使用的序列化/反序列化器
        // 之所以使用GSON，因为其兼容性极佳，有些许性能问题而已，不算个事
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        Gson gson = gsonBuilder.create();
        return new KOOKClientImpl(builder, config, publisher, gson);
    }

    @Bean
    @ConditionalOnMissingBean({MessageListener.class})
    public MessageListener kookClient(KOOKBotConfig config,
                                 ApplicationContext context,
                                      List<? extends Command> commandList) {
        return new MessageListener(config, context, commandList);
    }
}
