package site.chaotic.quantum.messagetransportserver.service;

import org.springframework.stereotype.Service;
import site.chaotic.quantum.messagetransportserver.util.MessageCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class BridgeService {
    private final Queue<MessageCard> messageQueueToDiscord = new LinkedBlockingQueue<>();
    private final Queue<MessageCard> messageQueueToKHL = new LinkedBlockingQueue<>();

    public void addToDiscord(MessageCard msg) {
        messageQueueToDiscord.offer(msg);
    }

    public void addToKHL(MessageCard msg) {
        messageQueueToKHL.offer(msg);
    }

    public List<MessageCard> clearToDiscord() {
        List<MessageCard> tmp = new ArrayList<>();
        MessageCard msg;
        while ((msg = messageQueueToDiscord.poll()) != null) {
            tmp.add(msg);
        }
        return tmp;
    }

    public List<MessageCard> clearToKHL() {
        List<MessageCard> tmp = new ArrayList<>();
        MessageCard msg;
        while ((msg = messageQueueToKHL.poll()) != null) {
            tmp.add(msg);
        }
        return tmp;
    }

    public String translateChannelId(String id) {
        if (id.equals("413997224880504834")) return "7569533375445017";
        if (id.equals("7569533375445017")) return "413997224880504834";
        return "";
    }
}
