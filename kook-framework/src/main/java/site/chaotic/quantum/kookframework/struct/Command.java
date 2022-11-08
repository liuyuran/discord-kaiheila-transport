package site.chaotic.quantum.kookframework.struct;

/**
 * bot命令抽象接口，任何命令的实现都需要实现这个接口
 */
public interface Command {
    /**
     * 定义命令前缀，即【/ac FireIV】中的【ac】
     *
     * @return 命令前缀
     */
    String getCommand();
    /**
     * 执行bot命令
     *
     * @param event 所有必需的文本命令信息
     * @throws Exception 任何在事件处理过程中抛出的错误
     */
    void execute(CommandEvent event) throws Exception;
}
