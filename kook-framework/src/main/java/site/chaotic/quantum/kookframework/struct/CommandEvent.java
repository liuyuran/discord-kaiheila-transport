package site.chaotic.quantum.kookframework.struct;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * bot命令事件，包含目前用到的需要透传的参数
 */
@Data
@AllArgsConstructor
public class CommandEvent {
    /*
     * 接收到命令的频道id
     * 似乎和最初想象中不太一样，对于机器人接口来说，每个服务器的每个频道都是独立的信道
     * 不知道以后会不会有变化
     */
    private String channelId;
    /*
     * 完整的文本命令
     * 如以【/ac FireIV】为例，其值为【/ac FireIV】
     */
    private String fullMessage;
    /*
     * 命令本身
     * 如以【/ac FireIV】为例，其值为【ac】
     *  */
    private String command;
    /*
     * 命令参数
     * 如以【/ac FireIV】为例，其值为【FireIV】
     *  */
    private String args;
}
