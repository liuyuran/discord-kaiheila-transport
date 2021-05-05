package site.chaotic.quantum.messagetransportserver.consts;

public class APIConst {
    public static final String KaiHLBaseUrl = "https://www.kaiheila.cn/api/v3";

    public static final String KaiHLApiGateway = "/gateway/index?compress=0";
    public static final String KaiHLApiMessageCreate = "/message/create";

    public static final int MSG_TYPE_API = 0;
    public static final int MSG_TYPE_HELLO = 1;
    public static final int MSG_TYPE_PING = 2;
    public static final int MSG_TYPE_PONG = 3;
    public static final int MSG_TYPE_RECONNECT = 5;
    public static final int MSG_TYPE_RESUME = 6;
}
