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
        while ((msg = messageQueueToDiscord.peek()) != null) {
            tmp.add(msg);
        }
        return tmp;
    }

    public List<MessageCard> clearToKHL() {
        List<MessageCard> tmp = new ArrayList<>();
        MessageCard msg;
        while ((msg = messageQueueToDiscord.peek()) != null) {
            tmp.add(msg);
        }
        return tmp;
    }
}
