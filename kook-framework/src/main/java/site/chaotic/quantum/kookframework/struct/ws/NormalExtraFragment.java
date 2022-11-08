package site.chaotic.quantum.kookframework.struct.ws;

import lombok.Data;

@Data
public class NormalExtraFragment {
    private Integer type;
    private String guildId;
    private String channelName;
    private String[] mention;
    private Boolean mentionAll;
    private String[] mentionRoles;
    private Boolean mentionHere;
    private UserFragment author;
}
