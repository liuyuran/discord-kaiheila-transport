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
import site.chaotic.quantum.messagetransportserver.consts.MessageType;
import site.chaotic.quantum.messagetransportserver.util.MessageCard;

import java.io.IOException;
import java.util.List;

import static site.chaotic.quantum.messagetransportserver.util.DownloadUtil.downloadImage;

/**
 * 开黑啦API服务
 */
@Service
@Log4j2
public class KookService {
    private final BridgeService bridgeService;
    private final KHLClient client;
    private final String correctPrefix;

    @Autowired
    public KookService(BridgeService bridgeService, KHLBotConfig config, KHLClient client) {
        this.bridgeService = bridgeService;
        this.correctPrefix = String.format("/%s", config.getCommandPrefix());
        this.client = client;
    }

    @Scheduled(cron = "*/10 * * * * ? ")
    public void syncMessage() throws IOException {
        List<MessageCard> messageCards = bridgeService.clearToKook();
        for (MessageCard card: messageCards) {
            if (bridgeService.translateChannelId(card.getChannelId()) == null) continue;
            switch (card.getType()) {
                case Text:
                    client.sendMessage(bridgeService.translateChannelId(card.getChannelId()), card.getContent()).subscribe();
                    break;
                case Image:
                    for (String url: card.getContent().split(",")) {
                        client.sendImage(bridgeService.translateChannelId(card.getChannelId()), downloadImage(url)).subscribe();
                    }
                    break;
                case Unknown:
                default:
                    break;
            }
        }
    }

    @EventListener
    public void processMessageEvent(BaseEvent<BaseMessageContent<NormalExtraFragment>> event) {
        String message = event.getData().getContent();
        MessageType msgType = MessageType.fromKookCode(event.getData().getType());
        if (msgType != MessageType.Text || !message.startsWith(correctPrefix)) return;
        String needTransport = message.replaceFirst(correctPrefix, "").trim();
        bridgeService.addToDiscord(new MessageCard(
                msgType,
                event.getData().getTargetId(),
                needTransport)
        );
    }
}
