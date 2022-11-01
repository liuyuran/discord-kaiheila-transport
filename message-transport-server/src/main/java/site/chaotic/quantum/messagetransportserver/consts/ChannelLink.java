package site.chaotic.quantum.messagetransportserver.consts;

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
