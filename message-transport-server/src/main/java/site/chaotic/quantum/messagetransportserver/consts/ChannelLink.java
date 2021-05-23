package site.chaotic.quantum.messagetransportserver.consts;

public enum ChannelLink {
    Test("413997224880504834", "7569533375445017");

    private final String discordId;
    private final String KHLId;
    ChannelLink(String discordId, String KHLId) {
        this.discordId = discordId;
        this.KHLId = KHLId;
    }

    public String getDiscordId() {
        return discordId;
    }

    public String getKHLId() {
        return KHLId;
    }
}
