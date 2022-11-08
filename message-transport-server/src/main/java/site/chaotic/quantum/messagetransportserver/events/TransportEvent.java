package site.chaotic.quantum.messagetransportserver.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.chaotic.quantum.kookframework.struct.Command;
import site.chaotic.quantum.kookframework.struct.CommandEvent;
import site.chaotic.quantum.kookframework.enums.MessageType;
import site.chaotic.quantum.messagetransportserver.service.BridgeService;
import site.chaotic.quantum.messagetransportserver.util.MessageCard;

@Service
public class TransportEvent implements Command {
    private final BridgeService bridgeService;

    @Autowired
    public TransportEvent(BridgeService bridgeService) {
        this.bridgeService = bridgeService;
    }

    @Override
    public void execute(CommandEvent event) {
        bridgeService.addToDiscord(new MessageCard(
                MessageType.Text,
                event.getChannelId(),
                event.getArgs())
        );
    }
}
