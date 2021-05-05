package site.chaotic.quantum.khlframework.struct.ws;

import lombok.Data;

@Data
public class UserFragment {
    private String id;
    private String username;
    private String identifyNum;
    private Boolean online = false;
    private Integer status;
    private String avatar;
    private Boolean bot = false;
    private Boolean mobileVerified = false;
    private Boolean system = false;
    private String mobilePrefix;
    private String mobile;
    private Integer invitedCount;
    private String nickName;
    private Integer[] roles;
}
