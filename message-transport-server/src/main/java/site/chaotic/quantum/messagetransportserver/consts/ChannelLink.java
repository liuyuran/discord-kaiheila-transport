package site.chaotic.quantum.messagetransportserver.consts;

/**
 * 频道绑定类，如果要写死配置，记得在这里修改绑定关系，如果要动态配置就得自己适配数据库了
 */
public enum ChannelLink {
    Test("413997224880504834", "7569533375445017");

    private final String discordId;
    private final String KookId;
    ChannelLink(String discordId, String KookId) {
        this.discordId = discordId;
        this.KookId = KookId;
    }

    public String getDiscordId() {
        return discordId;
    }

    public String getKookId() {
        return KookId;
    }
}
