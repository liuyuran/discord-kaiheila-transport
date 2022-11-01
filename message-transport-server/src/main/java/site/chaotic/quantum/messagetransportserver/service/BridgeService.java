package site.chaotic.quantum.messagetransportserver.service;

import org.springframework.stereotype.Service;
import site.chaotic.quantum.messagetransportserver.consts.ChannelLink;
import site.chaotic.quantum.messagetransportserver.util.MessageCard;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class BridgeService {
    private final Queue<MessageCard> messageQueueToDiscord = new LinkedBlockingQueue<>();
    private final Queue<MessageCard> messageQueueToKHL = new LinkedBlockingQueue<>();
    private static final HashMap<String, String> link = new HashMap<>();

    static {
        Arrays.stream(ChannelLink.values()).forEach(item -> {
            link.put(item.getDiscordId(), item.getKookId());
            link.put(item.getKookId(), item.getDiscordId());
        });
    }

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
        return link.getOrDefault(id, null);
    }
}
