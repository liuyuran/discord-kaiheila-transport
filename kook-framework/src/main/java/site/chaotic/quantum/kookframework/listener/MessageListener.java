package site.chaotic.quantum.kookframework.listener;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import site.chaotic.quantum.kookframework.config.KOOKBotConfig;
import site.chaotic.quantum.kookframework.enums.MessageType;
import site.chaotic.quantum.kookframework.struct.BaseEvent;
import site.chaotic.quantum.kookframework.struct.Command;
import site.chaotic.quantum.kookframework.struct.CommandEvent;
import site.chaotic.quantum.kookframework.struct.ws.BaseMessageContent;
import site.chaotic.quantum.kookframework.struct.ws.NormalExtraFragment;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 消息监听器，用于加载并运行各类命令事件
 */
public class MessageListener {
    private final static Pattern commandAnalysis = Pattern.compile("^(?<command>\\w+) +(?<content>.+)$");
    private final KOOKBotConfig config;
    private final ApplicationContext context;
    private final HashMap<String, Class<? extends Command>> commands;

    public MessageListener(KOOKBotConfig config, ApplicationContext context,
                           List<? extends Command> commandList) {
        this.config = config;
        this.context = context;
        commands = new HashMap<>();
        commandList.forEach(item -> {
            commands.put(item.getCommand(), item.getClass());
        });
    }
    /**
     * 顺带监听通过websocket推送过来的消息
     *
     * @param event 服务器消息
     * @throws Exception 处理命令时遇到的错误
     */
    @EventListener
    public void processMessageEvent(BaseEvent<BaseMessageContent<NormalExtraFragment>> event) throws Exception {
        if (commands == null) return;
        String message = event.getData().getContent();
        MessageType msgType = MessageType.fromKookCode(event.getData().getType());
        if (msgType != MessageType.Text || !message.startsWith(config.getPrefix())) return;
        String needAnalysis = message.replaceFirst(config.getPrefix(), "").trim();
        Matcher matcher = commandAnalysis.matcher(needAnalysis);
        if (!matcher.find()) return;
        String commandType = matcher.group("command");
        if (!commands.containsKey(commandType)) return;
        context.getBean(commands.get(commandType)).execute(new CommandEvent(
                event.getData().getTargetId(),
                message,
                commandType,
                matcher.group("content")
        ));
    }
}
