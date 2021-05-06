package site.chaotic.quantum.messagetransportserver.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
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
    private final KHLClient client;
    private final BridgeService bridgeService;

    public KaiHLService(BridgeService bridgeService, KHLClient client) {
        this.bridgeService = bridgeService;
        this.client = client;
    }

    @Scheduled(cron = "*/10 * * * * ? ")
    public void syncMessage() {
        List<MessageCard> messageCards = bridgeService.clearToKHL();
        for (MessageCard card: messageCards) {
            client.sendMessage(bridgeService.translateChannelId(card.getChannelId()), card.getContent());
        }
    }

    @EventListener
    public void processMessageEvent(BaseEvent<BaseMessageContent<NormalExtraFragment>> event) {
        bridgeService.addToDiscord(new MessageCard(
                event.getData().getTargetId(),
                String.format("%s [%s]: %s",
                        event.getData().getExtra().getAuthor().getNickName(),
                        event.getData().getAuthorId(),
                        event.getData().getContent())
        ));
    }
}
