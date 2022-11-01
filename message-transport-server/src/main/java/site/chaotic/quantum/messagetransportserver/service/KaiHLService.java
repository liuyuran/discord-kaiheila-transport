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
    private final BridgeService bridgeService;
    private final KHLClient client;
    private final String correctPrefix;

    @Autowired
    public KaiHLService(BridgeService bridgeService, KHLBotConfig config, KHLClient client) {
        this.bridgeService = bridgeService;
        this.correctPrefix = String.format("/%s", config.getCommandPrefix());
        this.client = client;
    }

    @Scheduled(cron = "*/10 * * * * ? ")
    public void syncMessage() {
        List<MessageCard> messageCards = bridgeService.clearToKHL();
        for (MessageCard card: messageCards) {
            if (bridgeService.translateChannelId(card.getChannelId()) == null) continue;
            client.sendMessage(bridgeService.translateChannelId(card.getChannelId()),
                    card.getContent()).subscribe();
        }
    }

    @EventListener
    public void processMessageEvent(BaseEvent<BaseMessageContent<NormalExtraFragment>> event) {
        String message = event.getData().getContent();
        if (!message.startsWith(correctPrefix)) return;
        String needTransport = message.replaceFirst(correctPrefix, "").trim();
        bridgeService.addToDiscord(new MessageCard(
                event.getData().getTargetId(),
                needTransport)
        );
    }
}
