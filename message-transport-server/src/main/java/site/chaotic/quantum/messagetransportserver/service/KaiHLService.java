package site.chaotic.quantum.messagetransportserver.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import site.chaotic.quantum.khlframework.config.KHLBotConfig;
import site.chaotic.quantum.khlframework.interfaces.KHLClient;
import site.chaotic.quantum.khlframework.struct.BaseEvent;
import site.chaotic.quantum.khlframework.struct.ws.BaseMessageContent;
import site.chaotic.quantum.khlframework.struct.ws.NormalExtraFragment;
import site.chaotic.quantum.messagetransportserver.util.MessageCard;

import java.util.List;

/**
 * 开黑啦API服务
 */
@Service
@Log4j2
public class KaiHLService {
    private final KHLBotConfig config;

    @Autowired
    public KaiHLService(KHLBotConfig config) {
        this.config = config;
    }

    @EventListener
    public void processMessageEvent(BaseEvent<BaseMessageContent<NormalExtraFragment>> event) {
        String message = event.getData().getContent();
        if (!message.startsWith(String.format("/%s", config.getCommandPrefix()))) return;
        String[] command = message.split(" ");
        log.info(String.format("[%s] %s", event.getData().getTargetId(), message));
    }
}
