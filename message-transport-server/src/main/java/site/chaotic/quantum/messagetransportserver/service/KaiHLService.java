package site.chaotic.quantum.messagetransportserver.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import site.chaotic.quantum.khlframework.interfaces.KHLClient;
import site.chaotic.quantum.khlframework.struct.BaseEvent;
import site.chaotic.quantum.khlframework.struct.ws.BaseMessageContent;
import site.chaotic.quantum.khlframework.struct.ws.NormalExtraFragment;

/**
 * 开黑啦API服务
 */
@Service
@Log4j2
public class KaiHLService {
    private final KHLClient client;
    private final BridgeService bridgeService;

    public KaiHLService(KHLClient client, BridgeService bridgeService) {
        this.client = client;
        this.bridgeService = bridgeService;
    }

    @EventListener
    public void processMessageEvent(BaseEvent<BaseMessageContent<NormalExtraFragment>> event) {
        log.info(event.getData().getContent());
        client.sendMessage(event.getData().getTargetId(), event.getData().getContent()).subscribe();
    }
}
