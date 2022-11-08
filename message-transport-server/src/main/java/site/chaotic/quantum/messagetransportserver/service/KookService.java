package site.chaotic.quantum.messagetransportserver.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import site.chaotic.quantum.kookframework.config.KOOKBotConfig;
import site.chaotic.quantum.kookframework.interfaces.KOOKClient;
import site.chaotic.quantum.kookframework.struct.Command;
import site.chaotic.quantum.messagetransportserver.util.MessageCard;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static site.chaotic.quantum.messagetransportserver.util.DownloadUtil.downloadImage;

/**
 * 轮询消息队列，将消息推送至KOOK
 */
@Service
@Log4j2
public class KookService {
    private final BridgeService bridgeService;
    private final KOOKClient client;

    @Autowired
    public KookService(BridgeService bridgeService, KOOKBotConfig config, KOOKClient client) {
        this.bridgeService = bridgeService;
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
}
