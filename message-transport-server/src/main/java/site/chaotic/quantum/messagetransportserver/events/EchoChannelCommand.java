package site.chaotic.quantum.messagetransportserver.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.chaotic.quantum.kookframework.enums.MessageType;
import site.chaotic.quantum.kookframework.interfaces.KOOKClient;
import site.chaotic.quantum.kookframework.struct.Command;
import site.chaotic.quantum.kookframework.struct.CommandEvent;
import site.chaotic.quantum.messagetransportserver.util.MessageCard;

@Service
public class EchoChannelCommand implements Command {
    private final KOOKClient kookClient;

    @Autowired
    public EchoChannelCommand(KOOKClient kookClient) {
        this.kookClient = kookClient;
    }

    @Override
    public String getCommand() {
        return "echo-channel";
    }

    @Override
    public void execute(CommandEvent event) {
        kookClient.sendMessage(
                event.getChannelId(),
                String.format("该频道的ID为：%s", event.getChannelId())
        ).block();
    }
}
