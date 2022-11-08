package site.chaotic.quantum.kookframework.struct;

/**
 * Bot命令抽象接口，任何命令的实现都需要实现这个接口
 */
public interface Command {
    void execute(CommandEvent event) throws Exception;
}
